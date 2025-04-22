package com.example.delivery_app.common.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(
					"/swagger-ui/**",
					"/swagger-ui.html",
					"/v3/api-docs/**",
					"/webjars/**"
				).permitAll()
				.anyRequest().authenticated()
			)
			.formLogin(login -> login.disable())
			.httpBasic(basic -> basic.disable());

		return http.build();
	}
}
