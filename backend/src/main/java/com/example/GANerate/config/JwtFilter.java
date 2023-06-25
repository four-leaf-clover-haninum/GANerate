package com.example.GANerate.config;

import com.example.GANerate.service.UserService;
import com.example.GANerate.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final String secretKey;

    //인증 전 권한을 부여하는 부분, 모든 요청에 권한 부여
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("authorization : {}", authorization);

        //토큰을 보내지 않으면 차단
        if(authorization == null || !authorization.startsWith("Bearer ")){
            log.error("authorization을 잘못 보냈습니다.");
            filterChain.doFilter(request,response);
            return;
        }

        //Token 꺼내기
        String token = authorization.split(" ")[1];

        //Token expire 여부
        if(JwtTokenUtil.isExpired(token,secretKey)){
            log.error("토큰이 만료되었습니다.");
            filterChain.doFilter(request,response);
            return;
        }

        //UserId 토큰에서 꺼내기
        String userId = JwtTokenUtil.getUserId(token, secretKey);
        log.info("userId: {}", userId);

        //권한 부여
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userId, null, List.of(new SimpleGrantedAuthority("USER")));

        //Detail 넣기
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request,response);
    }
}
