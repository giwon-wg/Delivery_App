package com.example.delivery_app.domain.user.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.delivery_app.common.exception.CustomException;
import com.example.delivery_app.common.jwt.JwtAuthenticationFilter;
import com.example.delivery_app.common.jwt.JwtTokenProvider;
import com.example.delivery_app.common.redis.dto.TokenRefreshRequest;
import com.example.delivery_app.common.redis.dto.TokenRefreshResponse;
import com.example.delivery_app.common.redis.service.BlackListService;
import com.example.delivery_app.common.redis.service.RefreshTokenService;
import com.example.delivery_app.domain.user.Auth.UserAuth;
import com.example.delivery_app.domain.user.dto.request.LoginRequest;
import com.example.delivery_app.domain.user.dto.request.OwnerApplyRequest;
import com.example.delivery_app.domain.user.dto.request.PasswordChangeRequest;
import com.example.delivery_app.domain.user.dto.request.SignUpRequest;
import com.example.delivery_app.domain.user.dto.request.UserProfileUpdateRequest;
import com.example.delivery_app.domain.user.dto.response.LoginResponse;
import com.example.delivery_app.domain.user.dto.response.UserProfileDto;
import com.example.delivery_app.domain.user.entity.User;
import com.example.delivery_app.domain.user.entity.UserRole;
import com.example.delivery_app.domain.user.exception.AuthErrorCode;
import com.example.delivery_app.domain.user.exception.UserErrorCode;
import com.example.delivery_app.domain.user.repository.UserRepository;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final RefreshTokenService refreshTokenService;
	private final RedisTemplate<String, String> redisTemplate;
	private final BlackListService blackListService;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	@Value("${spring.jwt.token.refresh.minute}")
	private long refreshTokenExpireMinute;

	/**
	 * 회원가입
	 *
	 * @param request
	 */
	@Override
	public void signUp(SignUpRequest request) {

		// 예외처리는 코드 작성 완료 후 예외처리 통일화 진행 예정
		if (userRepository.existsByEmailAndIsDeletedFalse(request.getEmail())) {
			throw new CustomException(UserErrorCode.DUPLICATE_EMAIL);
		}

		if (userRepository.existsByNicknameAndIsDeletedFalse(request.getNickname())) {
			throw new CustomException(UserErrorCode.DUPLICATE_NICKNAME);
		}

		String email = request.getEmail();
		String encodedPassword = passwordEncoder.encode(request.getPassword());

		User user;

		if (isAdminEmail(email)) {
			user = User.builder()
				.email(email)
				.password(encodedPassword)
				.nickname(request.getNickname())
				.role(UserRole.ADMIN) // 사내 이메일이면 자동 ADMIN
				.address(request.getAddress())
				.isDeleted(false)
				.build();
		} else {
			user = User.builder()
				.email(email)
				.password(encodedPassword)
				.nickname(request.getNickname())
				.role(UserRole.USER) // 기본은 USER
				.address(request.getAddress())
				.isDeleted(false)
				.build();
		}
		userRepository.save(user);
	}

	@Override
	public LoginResponse login(LoginRequest request) {
		User user = userRepository.findByEmail(request.getEmail())
			.orElseThrow(() -> new CustomException(UserErrorCode.EMAIL_NOT_FOUND));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new CustomException(UserErrorCode.PASSWORD_MISMATCH);
		}

		// AccessToken 발급
		String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), List.of(user.getRole()));

		// RefreshToken 발급
		String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

		//Redis 에 저장
		redisTemplate.opsForValue().set(
			"RT:" + user.getId(),
			refreshToken,
			7,
			TimeUnit.DAYS
		);
		log.info("[Redis 저장] RT:{} = {}", user.getId(), refreshToken);

		return new LoginResponse(accessToken, refreshToken);
	}

	@Override
	public TokenRefreshResponse reissue(TokenRefreshRequest refreshRequest, HttpServletRequest request) {

		String refreshToken = refreshRequest.getRefreshToken();

		if (refreshToken == null || refreshToken.isBlank()) {
			throw new CustomException(AuthErrorCode.NULL_REFRESH_TOKEN);
		}

		if(!jwtTokenProvider.validateToken(refreshToken)) {
			throw new CustomException(AuthErrorCode.INVALID_REFRESH_TOKEN);
		}

		//기존 토큰 블랙리스트 추가
		String oldAccessToken = jwtTokenProvider.resolveAccessTokenFromContext(request);
		if (oldAccessToken != null && jwtTokenProvider.validateToken(oldAccessToken)) {
			Date expiration = jwtTokenProvider.getExpiration(oldAccessToken);

			long remaining = expiration.getTime() - System.currentTimeMillis();

			if (remaining > 0) {
				blackListService.addToBlacklist(oldAccessToken, remaining);
			}
		}

		Claims claims = jwtTokenProvider.getClaims(refreshToken);

		Long userId = Long.parseLong(claims.getSubject());

		String storedToken = refreshTokenService.get(userId);

		if (storedToken == null || !storedToken.equals(refreshToken)) {
			throw new CustomException(AuthErrorCode.INVALID_REFRESH_TOKEN);
		}

		User user = userRepository.findActiveById(userId)
			.orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

		UserRole role = user.getRole();
		if (role == null) {
			throw new CustomException(UserErrorCode.USER_ROLE_NOT_DEFINED);
		}

		String newAccessToken = jwtTokenProvider.generateAccessToken(user.getId(), List.of(user.getRole()));
		String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId());
		long refreshTokenExpireTime = refreshTokenExpireMinute * 60 * 1000;
		refreshTokenService.save(userId, newRefreshToken, refreshTokenExpireTime);

		return new TokenRefreshResponse(newAccessToken, newRefreshToken);
	}

	@Override
	public void logout(Long userId, String accessToken) {

		//refreshToken 제거
		refreshTokenService.delete(userId);

		Date expiration = jwtTokenProvider.getExpiration(accessToken);
		long now = System.currentTimeMillis();
		long remainingTime = expiration.getTime() - now;

		if (remainingTime > 0) {
			blackListService.addToBlacklist(accessToken, remainingTime);
		}

	}

	@Override
	public UserProfileDto getProfile(Long id, boolean isPrivate) {
		User user = userRepository.findActiveById(id)
			.orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

		if (isPrivate) {
			return UserProfileDto.fromPrivate(user); // 전체 정보 조회
		} else {
			return UserProfileDto.fromPublic(user);
		}
	}

	@Override
	public void updateProfile(Long id, UserAuth currentUser, UserProfileUpdateRequest request) {

		User user = userRepository.findActiveById(id)
			.orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

		if (!currentUser.getId().equals(id) && !currentUser.hasRole("ADMIN")) {
			throw new CustomException(UserErrorCode.ACCESS_DENIED);
		}

		if (!user.getNickname().equals(request.getNickname())
			&& userRepository.existsByNicknameAndIsDeletedFalse(request.getNickname())) {
			throw new CustomException(UserErrorCode.DUPLICATE_NICKNAME);
		}

		user.updateProfile(request.getNickname(), request.getAddress());
		userRepository.save(user);
	}

	@Override
	@Transactional
	public void changePassword(Long targetId, UserAuth currentUser, PasswordChangeRequest request) {

		User user = userRepository.findActiveById(targetId)
			.orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

		if (!currentUser.getId().equals(targetId) && !currentUser.hasRole("ADMIN")) {
			throw new CustomException(UserErrorCode.ACCESS_DENIED);
		}

		// 어드민이 아닌 경우, 현재 비밀번호 확인 필요
		if (!currentUser.hasRole("ADMIN")) {
			if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
				throw new CustomException(UserErrorCode.PASSWORD_MISMATCH);
			}
		}

		if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
			throw new CustomException(UserErrorCode.PASSWORD_CONFIRM_MISMATCH);
		}

		String encodedPassword = passwordEncoder.encode(request.getNewPassword());
		user.changePassword(encodedPassword);
	}

	@Override
	public void deleteAccount(Long targetId, UserAuth currentUser, String token) {
		if (!currentUser.getId().equals(targetId) && !currentUser.hasRole("ADMIN")) {
			throw new CustomException(UserErrorCode.ACCESS_DENIED);
		}

		if (currentUser.getId().equals(targetId) && currentUser.hasRole("ADMIN")) {
			throw new CustomException(UserErrorCode.ADMIN_SELF_DELETE_BLOCKED);
		}

		User user = userRepository.findActiveById(targetId)
			.orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

		user.delete();
		userRepository.save(user);

		if (token != null && jwtTokenProvider.validateToken(token)) {
			Date expiration = jwtTokenProvider.getExpiration(token);
			long remaining = expiration.getTime() - System.currentTimeMillis();
			if (remaining > 0) {
				blackListService.addToBlacklist(token, remaining);
			}
		}
	}

	@Override
	public void deleteAccountByAdmin(Long targetId, UserAuth currentUser) {

		if (!currentUser.getId().equals(targetId) && !currentUser.hasRole("ADMIN")) {
			throw new CustomException(UserErrorCode.ACCESS_DENIED);
		}

		if (currentUser.getId().equals(targetId) && currentUser.hasRole("ADMIN")) {
			throw new CustomException(UserErrorCode.ADMIN_SELF_DELETE_BLOCKED);
		}

		User user = userRepository.findActiveById(targetId)
			.orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

		user.delete();
		userRepository.save(user);
	}

	@Override
	@Transactional
	public void applyForBusiness(OwnerApplyRequest request, UserAuth currentUser) {
		User user = userRepository.findActiveById(currentUser.getId())
			.orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

		if (user.getRole() == UserRole.OWNER) {
			throw new CustomException(UserErrorCode.USER_ALREADY_OWNER);
		}

		log.info("사업자 신청: {}, 등록번호: {}", request.getOwnerName(), request.getRegistrationNumber());

		user.changeRole(UserRole.OWNER);
	}

	@Override
	public User registerIfNeed(String email) {
		return userRepository.findByEmail(email)
			.orElseGet(() -> {
				// 없으면 새로 등록
				User newUser = User.builder()
					.email(email)
					.password("SOCIAL2025!")
					.nickname("구글유저_" + UUID.randomUUID().toString().substring(0, 8))
					.address("소셜 로그인 유저")
					.role(UserRole.USER)
					.isDeleted(false)
					.build();

				return userRepository.save(newUser);
			});
	}

	private boolean isAdminEmail(String email) {
		return email != null && email.matches(".*@deliveryhajo\\.com$");
	}
}
