package com.example.delivery_app.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

	// 사용자 인증에 사용하는 토큰
	private String accessToken;

	// 액세스 토큰 갱신에 사용하는 토큰
	private String refreshToken;

}
