package com.example.GANerate.config;

import com.example.GANerate.repository.UserRepository;
import com.example.GANerate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserService userService;

    @Value("${jwt.token.secret}")
    private String secretKey;
//
//    @Autowired
//    private CorsConfig corsConfig;

//    @Bean
//    public AuthenticationManager authenticationManager() throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        //REST API를 위한 설정
        return httpSecurity
                .httpBasic().disable()//기본 설정 사용 x, 요청 헤더에 id, pw를 보내는 방식임
                .csrf().disable()//csrf 공격 방어 해제(rest api에서는 csrf 필용 x)
                .cors().and()
                .authorizeRequests()//URL에 따른 페이지 권한 부여 시작
                .antMatchers("/users/join", "/users/login","/main/**").permitAll() //join, login, /main/~~은 토큰 없어도 가능
                .anyRequest().authenticated() //그 외의 url은 인증 필요
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)//세션 사용 x, 서버 무상태 유지
                .and()
                .addFilterBefore(new JwtFilter(userService, secretKey), UsernamePasswordAuthenticationFilter.class)
                .build();
    }


}
