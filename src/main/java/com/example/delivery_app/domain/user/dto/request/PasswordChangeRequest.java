package com.example.delivery_app.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PasswordChangeRequest {

	@NotBlank(message = "현재 비밀번호는 필수입니다.")
	private String currentPassword;

	@NotBlank(message = "새 비밀번호는 필수입니다.")
	private String newPassword;

	@NotBlank(message = "새 비밀번호 확인은 필수입니다.")
	private String confirmNewPassword;
}
