package com.example.delivery_app.domain.user.dto;

import com.example.delivery_app.domain.user.entity.UserRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpRequest {

	@NotBlank
	@Email
	private String email;

	@NotBlank
	private String password;

	@NotBlank
	private String nickname;

	@NotNull
	private UserRole role;

	@NotBlank
	private String address;
}
