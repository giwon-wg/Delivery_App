package com.example.delivery_app.domain.user.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.delivery_app.common.dto.CommonResponseDto;
import com.example.delivery_app.common.jwt.JwtTokenProvider;
import com.example.delivery_app.domain.user.dto.response.LoginResponse;
import com.example.delivery_app.domain.user.entity.User;
import com.example.delivery_app.domain.user.entity.UserRole;
import com.example.delivery_app.domain.user.exception.UserSuccessCode;
import com.example.delivery_app.domain.user.repository.UserRepository;
import com.example.delivery_app.domain.user.service.UserService;
import com.nimbusds.oauth2.sdk.TokenResponse;

@RestController
public class LoginSuccessController {

	private final UserRepository userRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final UserService userService;

	public LoginSuccessController(UserRepository userRepository, JwtTokenProvider jwtTokenProvider,
		UserService userService) {
		this.userRepository = userRepository;
		this.jwtTokenProvider = jwtTokenProvider;
		this.userService = userService;
	}

	@GetMapping("/login/success")
	public ResponseEntity<CommonResponseDto<LoginResponse>> success(@AuthenticationPrincipal OAuth2User oAuth2User) {
		String email = oAuth2User.getAttribute("email");

		User user = userService.registerIfNeed(email);

		String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), List.of(user.getRole()));
		String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

		LoginResponse loginResponse = new LoginResponse(accessToken, refreshToken);
		return ResponseEntity.ok(CommonResponseDto.of(UserSuccessCode.SUCCESS, loginResponse));
	}
}
