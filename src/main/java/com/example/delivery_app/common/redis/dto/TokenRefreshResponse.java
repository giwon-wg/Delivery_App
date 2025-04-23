package com.example.delivery_app.common.redis.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenRefreshResponse {

	private String accessToken;

	private String refreshToken;

}
