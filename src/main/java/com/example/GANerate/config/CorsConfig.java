package com.example.GANerate.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:8080", "http://3.35.255.4:8080", "http://3.35.255.4:8081", "http://3.35.255.4:80")
//                .exposedHeaders("Authorization") // 'Authorization' 헤더 값을 받아온다
                .allowedMethods(
                        "GET","POST","PUT","PATCH","DELETE","OPTIONS"
                )
                .allowCredentials(true);
    }
}