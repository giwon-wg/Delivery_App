package com.example.delivery_app.domain.user.service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.delivery_app.common.jwt.JwtAuthenticationFilter;
import com.example.delivery_app.common.jwt.JwtTokenProvider;
import com.example.delivery_app.common.redis.dto.TokenRefreshRequest;
import com.example.delivery_app.common.redis.dto.TokenRefreshResponse;
import com.example.delivery_app.common.redis.service.BlackListService;
import com.example.delivery_app.common.redis.service.RefreshTokenService;
import com.example.delivery_app.domain.user.dto.request.LoginRequest;
import com.example.delivery_app.domain.user.dto.request.SignUpRequest;
import com.example.delivery_app.domain.user.dto.response.LoginResponse;
import com.example.delivery_app.domain.user.dto.response.UserProfileDto;
import com.example.delivery_app.domain.user.entity.User;
import com.example.delivery_app.domain.user.entity.UserRole;
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
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new IllegalArgumentException("이미 등록된 이메일입니다.");
		}

		if (userRepository.existsByNickname(request.getNickname())) {
			throw new IllegalArgumentException("이미 존재하는 닉네임 입니다.");
		}

		String encodedPassword = passwordEncoder.encode(request.getPassword());

		User user = User.builder()
			.email(request.getEmail())
			.password(encodedPassword)
			.nickname(request.getNickname())
			.role(request.getRole())
			.address(request.getAddress())
			.isDeleted(false)
			.build();

		userRepository.save(user);
	}

	@Override
	public LoginResponse login(LoginRequest request) {
		User user = userRepository.findByEmail(request.getEmail())
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일 입니다."));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new IllegalArgumentException("비밀번호가 일치 하지 않습니다.");
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
			throw new IllegalArgumentException("RefreshToken이 비여 있거나, null입니다.");
		}

		if(!jwtTokenProvider.validateToken(refreshToken)) {
			throw new IllegalArgumentException("유효하지 않은 RefreshToken 입니다.");
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
		log.info("[리프레시 요청] 추출된 엑세스토큰 = {}", oldAccessToken);

		Claims claims = jwtTokenProvider.getClaims(refreshToken);

		Long userId = Long.parseLong(claims.getSubject());


		String storedToken = refreshTokenService.get(userId);

		if (storedToken == null || !storedToken.equals(refreshToken)) {
			throw new IllegalArgumentException("서버에 저장된 RefreshToken 과 다릅니다.");
		}

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		UserRole role = user.getRole();
		if (role == null) {
			throw new IllegalStateException("userRole()이 null입니다.");
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
		User user = userRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("유저 정보 없음"));

		if (isPrivate) {
			return UserProfileDto.fromPrivate(user); // 전체 정보 조회
		} else {
			return UserProfileDto.fromPublic(user);
		}
	}

}
