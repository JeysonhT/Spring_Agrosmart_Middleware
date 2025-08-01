package com.agrosmart.middleware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MiddlewareForAgrosmartAndGeminiApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MiddlewareForAgrosmartAndGeminiApiApplication.class, args);
	}

}
