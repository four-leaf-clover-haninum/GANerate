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

                //이후에 프론트 ec2의 아이피로 아래의 3000번을 변경.
//                .allowedOrigins("http://localhost:3000", "http://localhost:8080", "http://3.35.255.4:8080", "http://3.35.255.4:8081", "http://3.35.255.4:80", "http://3.35.255.4:8082")
                .allowedOrigins("http://localhost:3000", "http://localhost:8080", "http://3.35.255.4") //혹은 http://3.35.255.4:80 이것 까지 열어보기

//                .exposedHeaders("Authorization") // 'Authorization' 헤더 값을 받아온다
                .allowedMethods(
                        "GET","POST","PUT","PATCH","DELETE","OPTIONS"
                )
                //"http://210.94.182.28:3000"
                .allowCredentials(true);
    }
}