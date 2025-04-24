package com.example.delivery_app.domain.user.service;

import com.example.delivery_app.common.redis.dto.TokenRefreshRequest;
import com.example.delivery_app.common.redis.dto.TokenRefreshResponse;
import com.example.delivery_app.domain.user.dto.request.LoginRequest;
import com.example.delivery_app.domain.user.dto.request.SignUpRequest;
import com.example.delivery_app.domain.user.dto.response.LoginResponse;
import com.example.delivery_app.domain.user.dto.response.UserProfileDto;

import jakarta.servlet.http.HttpServletRequest;

public interface UserService {

	void signUp(SignUpRequest request);

	LoginResponse login(LoginRequest request);

	TokenRefreshResponse reissue(TokenRefreshRequest refreshRequest, HttpServletRequest request);

	void logout(Long userId, String accessToken);

	UserProfileDto getProfile(Long id, boolean isPrivate);
}
