package com.example.delivery_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class DeliveryAppApplication {
	public static void main(String[] args) {
		// dotenv 환경변수 설정
		Dotenv dotenv = Dotenv.load();
		System.setProperty("MYSQL_USERNAME", dotenv.get("MYSQL_USERNAME"));
		System.setProperty("MYSQL_PASSWORD", dotenv.get("MYSQL_PASSWORD"));
		System.setProperty("MYSQL_URL", dotenv.get("MYSQL_URL"));

		SpringApplication.run(DeliveryAppApplication.class, args);
	}
}
