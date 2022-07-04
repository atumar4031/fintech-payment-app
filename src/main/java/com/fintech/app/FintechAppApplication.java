package com.fintech.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class FintechAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(FintechAppApplication.class, args);
	}

	@Bean
	public JavaMailSender mailSender() {
		return new JavaMailSenderImpl();
	}
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}