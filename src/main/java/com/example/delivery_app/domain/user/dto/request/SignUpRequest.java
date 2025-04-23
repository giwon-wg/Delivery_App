package com.example.delivery_app.domain.user.dto.request;

import com.example.delivery_app.domain.user.entity.UserRole;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpRequest {

	@Schema(description = "이메일", example = "test@test.com")
	@NotBlank
	@Email
	private String email;

	@Schema(description = "비밀번호", example = "test1234!")
	@NotBlank
	private String password;

	@Schema(description = "닉네임", example = "내베캠")
	@NotBlank
	private String nickname;

	@Schema(description = "역할", example = "USER")
	@NotNull
	private UserRole role;

	@Schema(description = "주소", example = "울릉도 동남쪽 뱃길따라 200리")
	@NotBlank
	private String address;
}
