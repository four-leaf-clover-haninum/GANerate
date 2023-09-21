package com.example.GANerate.config.jwt;

import com.example.GANerate.config.redis.RedisUtil;
import com.example.GANerate.enumuration.Result;
import com.example.GANerate.exception.CustomException;
import com.example.GANerate.request.user.UserRequest;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.token-validity-in-seconds}")
    private long accessTokenValidTime;
    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenValidTime;
    @Value("${jwt.response.header}")
    private String jwtHeader;
    private static final String AUTHORITIES_KEY = "auth";

    private Key key;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private RedisUtil redisUtil;


    // 객체 초기화, secretKey를 Base64로 인코딩한다.
    @PostConstruct
    protected void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    //JWT access 토큰 생성
    public String createToken(Long userId, Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.accessTokenValidTime);

        String accessToken =  Jwts.builder()
                .setSubject(userId.toString())
                .claim(AUTHORITIES_KEY, authorities)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        return accessToken;
    }

    //Refresh token 생성
    public String createRefreshToken(Long userId, Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.refreshTokenValidTime);

        String refreshToken =  Jwts.builder()
                .setSubject(userId.toString())
//                .claim(AUTHORITIES_KEY, authorities) 왜 리프레시 토큰은 claim 설정을 빼지?? => 리프레시 토큰은 사용자 권한정보를 포함 시킬 이유가 없음.
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        redisUtil.setDataExpire(userId.toString(), refreshToken, this.refreshTokenValidTime);
        return refreshToken;
    }

    // access Token 재발급(재발급은 accessToken이 만료 되기 직전에 실행됨)
    public String reissue(UserRequest.reissue request){

        // 1. refresh token 검증
        validateToken(request.getRefreshToken());

        // 2. access token에서 userId(setSubject) 가져오기
        Authentication authentication = getAuthentication(request.getAccessToken());

        // 3. redis에서 userId 기반 저장된 refresh token 값을 가져옴.
        String refreshToken = redisUtil.getData(authentication.getName());
        if(!refreshToken.equals(request.getRefreshToken())){
            throw new CustomException(Result.INVALID_REFRESH_TOKEN);
        }

        // 로그아웃시 redis에 refresh token이 존재하지 않는 경우 처리
        if(ObjectUtils.isEmpty(refreshToken)){
            throw new CustomException(Result.BAD_REQUEST);
        }
        if(!refreshToken.equals(request.getRefreshToken())) {
            throw new CustomException(Result.INVALID_REFRESH_TOKEN);
        }

        // 4. 새로운 access token 생성
        String accessToken = createToken(Long.valueOf(authentication.getName()), authentication);

        // 5. refresh token redis 업데이트
        redisUtil.setDataExpire(authentication.getName(), accessToken, this.refreshTokenValidTime);

        return accessToken;
    }

    // Token 남은 유효시간
    public Long getExpiration(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        // 현재 시간
        Long now = new Date().getTime();
        return (expiration.getTime() - now);
    }

    //유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    //Token에 담겨있는 정보를 이용해 Authentication 객체 리턴
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        if(claims.get("auth") == null){
            throw new CustomException(Result.UNAUTHORITY_TOKEN);
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        Long userId = Long.valueOf(claims.getSubject());

        return new UsernamePasswordAuthenticationToken(userId, token, authorities);
//        UserDetails principal = new User(claims.getSubject(), "", authorities);
//        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
        response.setHeader(jwtHeader, accessToken);
    }

    // 토큰으로 회원 정보 조회
    public Long getUserId(String token) {
        return Long.valueOf(Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject());
    }
}
