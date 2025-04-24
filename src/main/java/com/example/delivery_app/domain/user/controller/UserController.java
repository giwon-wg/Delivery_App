package com.example.delivery_app.domain.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.delivery_app.common.jwt.JwtAuthenticationFilter;
import com.example.delivery_app.common.jwt.JwtTokenProvider;
import com.example.delivery_app.common.redis.dto.TokenRefreshRequest;
import com.example.delivery_app.common.redis.dto.TokenRefreshResponse;
import com.example.delivery_app.common.redis.service.RefreshTokenService;
import com.example.delivery_app.domain.user.Auth.UserAuth;
import com.example.delivery_app.domain.user.dto.request.LoginRequest;
import com.example.delivery_app.domain.user.dto.request.SignUpRequest;
import com.example.delivery_app.domain.user.dto.response.LoginResponse;
import com.example.delivery_app.domain.user.dto.response.UserProfileDto;
import com.example.delivery_app.domain.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Auth", description = "회원가입 and 로그인 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	@Operation(summary = "회원가입", description = "email, password, nickname, role, address 을 입력받아 회원가입")
	@PostMapping("/signup")
	public ResponseEntity<Void> signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
		userService.signUp(signUpRequest);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@Operation(summary = "로그인", description = "email, password 로 로그인 후 토큰 발급")
	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {

		return ResponseEntity.ok(userService.login(request));
	}

	@Operation(
		summary = "로그아웃",
		description = "로그아웃 후 토큰 말소",
		security = {@SecurityRequirement(name = "bearerAuth")}
	)
	@PostMapping("/logout")
	public ResponseEntity<String> logout(HttpServletRequest request) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null || !(auth.getPrincipal() instanceof UserAuth user)) {
			throw new IllegalArgumentException("로그인 유저 정보가 없습니다.");
		}

		Long userId = user.getId();
		String token = jwtAuthenticationFilter.resolveToken(request);

		userService.logout(userId, token);

		return ResponseEntity.ok("로그아웃되었습니다. (User ID: " + userId + ")");
	}

	/**
	 * 토큰 재발급
	 *
	 * @param refreshRequest
	 * @return 리프레쉬토큰을 이용해 엑세스 토큰 재발급
	 */
	@Operation(
		summary = "토큰 재발급",
		description = "리프레쉬 토큰을 이용해 엑세스토큰을 재발급"
	)
	@PostMapping("/reissue")
	public ResponseEntity<TokenRefreshResponse> reissue(@RequestBody TokenRefreshRequest refreshRequest, HttpServletRequest request) {
		return ResponseEntity.ok(userService.reissue(refreshRequest, request));
	}

	@Operation(summary = "내 프로필 조회", security = {@SecurityRequirement(name = "bearerAuth")})
	@GetMapping("/profiles/me")
	public ResponseEntity<UserProfileDto> getMyProfile(@AuthenticationPrincipal UserAuth user) {
		UserProfileDto profile = userService.getProfile(user.getId(), true);
		return ResponseEntity.ok(profile);
	}

	@Operation(summary = "프로필 조회", security = {@SecurityRequirement(name = "bearerAuth")})
	@GetMapping("/profiles/{id}")
	public ResponseEntity<UserProfileDto> getProfile(
		@PathVariable Long id,
		@AuthenticationPrincipal UserAuth user
	) {
		boolean isOwnerOrAdmin = user.getId().equals(id) || user.hasRole("ADMIN");
		UserProfileDto profile = userService.getProfile(id, isOwnerOrAdmin);
		return ResponseEntity.ok(profile);
	}


}
