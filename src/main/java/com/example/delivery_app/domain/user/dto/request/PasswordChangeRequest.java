package com.example.delivery_app.domain.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PasswordChangeRequest {

	@NotBlank(message = "현재 비밀번호는 필수입니다.")
	@JsonProperty("currentPassword")
	private String currentPassword;

	@NotBlank(message = "새 비밀번호는 필수입니다.")
	@JsonProperty("newPassword")
	private String newPassword;

	@NotBlank(message = "새 비밀번호 확인은 필수입니다.")
	@JsonProperty("confirmNewPassword")
	private String confirmNewPassword;
}
