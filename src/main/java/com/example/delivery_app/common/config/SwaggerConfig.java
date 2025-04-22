package com.example.delivery_app.common.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI deliveryAppOpenAPI() {
		return new OpenAPI()
			.info(new Info()
				.title("배달 API 문서")
				.description("팀 프로젝트 - Spring Boot 기반 배달 플랫폼 백엔드 API 명세")
				.version("v0.1.0")
				.contact(new Contact()
					.name("4조")
					.email("giwon.git@gmail.com"))
				.license(new License()
					.name("MIT License")
					.url("https://opensource.org/licenses/MIT"))
			)
			.externalDocs(new ExternalDocumentation()
				.description("깃허브 레포지토리")
				.url("https://github.com/giwon-wg/Delivery_App.git"));
	}

	@Bean
	public GroupedOpenApi publicApi() {
		return GroupedOpenApi.builder()
			.group("전체 API")
			.pathsToMatch("/**") // 모든 경로 문서화
			.build();
	}
}