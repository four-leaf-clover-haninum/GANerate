package com.example.GANerate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class GaNerateApplication {

	public static void main(String[] args) {
		SpringApplication.run(GaNerateApplication.class, args);
	}

}
