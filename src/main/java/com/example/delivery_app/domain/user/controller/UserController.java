package com.example.delivery_app.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.delivery_app.common.dto.CommonResponseDto;
import com.example.delivery_app.common.jwt.JwtAuthenticationFilter;
import com.example.delivery_app.common.redis.dto.TokenRefreshRequest;
import com.example.delivery_app.common.redis.dto.TokenRefreshResponse;
import com.example.delivery_app.domain.user.Auth.UserAuth;
import com.example.delivery_app.domain.user.dto.request.LoginRequest;
import com.example.delivery_app.domain.user.dto.request.OwnerApplyRequest;
import com.example.delivery_app.domain.user.dto.request.PasswordChangeRequest;
import com.example.delivery_app.domain.user.dto.request.SignUpRequest;
import com.example.delivery_app.domain.user.dto.request.UserProfileUpdateRequest;
import com.example.delivery_app.domain.user.dto.response.LoginResponse;
import com.example.delivery_app.domain.user.dto.response.UserProfileDto;
import com.example.delivery_app.domain.user.exception.UserSuccessCode;
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
	public ResponseEntity<CommonResponseDto<Void>> signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
		userService.signUp(signUpRequest);
		return ResponseEntity.ok(CommonResponseDto.of(UserSuccessCode.CREATED));
	}

	@Operation(summary = "로그인", description = "email, password 로 로그인 후 토큰 발급")
	@PostMapping("/login")
	public ResponseEntity<CommonResponseDto<LoginResponse>> login(@RequestBody @Valid LoginRequest request) {
		return ResponseEntity.ok(CommonResponseDto.of(UserSuccessCode.SUCCESS, userService.login(request)));
	}

	@Operation(
		summary = "로그아웃",
		description = "로그아웃 후 토큰 말소",
		security = {@SecurityRequirement(name = "bearerAuth")}
	)
	@PostMapping("/logout")
	public ResponseEntity<CommonResponseDto<String>> logout(HttpServletRequest request) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null || !(auth.getPrincipal() instanceof UserAuth user)) {
			throw new IllegalArgumentException("로그인 유저 정보가 없습니다.");
		}

		Long userId = user.getId();
		String token = jwtAuthenticationFilter.resolveToken(request);

		userService.logout(userId, token);

		return ResponseEntity.ok(CommonResponseDto.of(UserSuccessCode.SUCCESS, "로그아웃되었습니다. (User ID: " + userId + ")"));
	}

	/**
	 * 토큰 재발급
	 *
	 * @return 리프레쉬토큰을 이용해 엑세스 토큰 재발급
	 */
	@Operation(
		summary = "토큰 재발급",
		description = "리프레쉬 토큰을 이용해 엑세스토큰을 재발급"
	)
	@PostMapping("/reissue")
	public ResponseEntity<CommonResponseDto<TokenRefreshResponse>> reissue(@RequestBody TokenRefreshRequest request, HttpServletRequest servletRequest) {
		return ResponseEntity.ok(CommonResponseDto.of(UserSuccessCode.SUCCESS, userService.reissue(request, servletRequest)));
	}

	@Operation(summary = "내 프로필 조회", security = {@SecurityRequirement(name = "bearerAuth")})
	@GetMapping("/profiles/me")
	public ResponseEntity<CommonResponseDto<UserProfileDto>> getMyProfile(@AuthenticationPrincipal UserAuth user) {
		return ResponseEntity.ok(CommonResponseDto.of(UserSuccessCode.SUCCESS, userService.getProfile(user.getId(), true)));
	}

	@Operation(summary = "프로필 조회", security = {@SecurityRequirement(name = "bearerAuth")})
	@GetMapping("/profiles/{id}")
	public  ResponseEntity<CommonResponseDto<UserProfileDto>> getProfile(
		@PathVariable Long id,
		@AuthenticationPrincipal UserAuth user
	) {
		boolean isOwnerOrAdmin = user.getId().equals(id) || user.hasRole("ADMIN");
		UserProfileDto profile = userService.getProfile(id, isOwnerOrAdmin);
		return ResponseEntity.ok(CommonResponseDto.of(UserSuccessCode.SUCCESS, profile));
	}

	@Operation(summary = "프로필 수정", security = {@SecurityRequirement(name = "bearerAuth")})
	@PatchMapping("/profiles/{id}")
	public ResponseEntity<CommonResponseDto<Void>> updateProfile(
		@PathVariable Long id,
		@RequestBody @Valid UserProfileUpdateRequest request,
		@AuthenticationPrincipal UserAuth user
	) {
		userService.updateProfile(id, user, request);
		return ResponseEntity.ok(CommonResponseDto.of(UserSuccessCode.CREATED));
	}

	@Operation(summary = "비밀번호 변경", security = {@SecurityRequirement(name = "bearerAuth")})
	@PatchMapping("/profiles/{id}/password")
	public ResponseEntity<CommonResponseDto<Void>> changePassword(
		@PathVariable Long id,
		@RequestBody @Valid PasswordChangeRequest request,
		@AuthenticationPrincipal UserAuth user
	) {
		userService.changePassword(id, user, request);
		return ResponseEntity.ok(CommonResponseDto.of(UserSuccessCode.CREATED));
	}

	@DeleteMapping("/users/me")
	@Operation(summary = "회원 탈퇴", security = {@SecurityRequirement(name = "bearerAuth")})
	public ResponseEntity<CommonResponseDto<Void>> deleteMyAccount(
		HttpServletRequest request,
		@AuthenticationPrincipal UserAuth user
	) {
		String token = jwtAuthenticationFilter.resolveToken(request);
		userService.deleteAccount(user.getId(), user, token);
		return ResponseEntity.ok(CommonResponseDto.of(UserSuccessCode.DELETED));
	}

	@Operation(summary = "회원 강제 탈퇴", description = "관리자만 접근 가능", security = {@SecurityRequirement(name = "bearerAuth")})
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/users/{id}")
	public ResponseEntity<CommonResponseDto<Void>> deleteUserByAdmin(@PathVariable Long id, @AuthenticationPrincipal UserAuth user) {
		userService.deleteAccountByAdmin(id, user);
		return ResponseEntity.ok(CommonResponseDto.of(UserSuccessCode.DELETED));
	}

	@Operation(summary = "사업자 권한 신청", security = {@SecurityRequirement(name = "bearerAuth")})
	@PostMapping("/business/apply")
	public ResponseEntity<CommonResponseDto<Void>> applyForBusiness(
		@RequestBody @Valid OwnerApplyRequest request,
		@AuthenticationPrincipal UserAuth user
	) {
		userService.applyForBusiness(request, user);
		return ResponseEntity.ok(CommonResponseDto.of(UserSuccessCode.OWNER_GRANTED));
	}

}
