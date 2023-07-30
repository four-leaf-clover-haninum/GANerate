package com.example.GANerate.config.jwt;

import com.example.GANerate.config.redis.RedisUtil;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private TokenProvider tokenProvider;
    private RedisUtil redisUtil;

    public JwtSecurityConfig(TokenProvider tokenProvider, RedisUtil redisUtil) {
        this.tokenProvider = tokenProvider;
        this.redisUtil = redisUtil;
    }

    @Override
    public void configure(HttpSecurity http) {
        JwtAuthenticationFilter customFilter = new JwtAuthenticationFilter(tokenProvider, redisUtil);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }
}