package com.fintech.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class FintechAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(FintechAppApplication.class, args);
//		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//		System.out.println(passwordEncoder.encode("1234"));
	}
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}


//#    url: jdbc:mysql://localhost:3306/fintech-app
//		#    username: root
//		#    password: atumar4031