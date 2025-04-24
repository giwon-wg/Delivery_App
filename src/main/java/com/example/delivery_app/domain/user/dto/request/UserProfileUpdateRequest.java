package com.example.delivery_app.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserProfileUpdateRequest {

	@NotBlank(message = "닉네임은 필수 입니다.")
	private String nickname;

	private String address;
}
