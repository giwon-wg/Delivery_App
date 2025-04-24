package com.example.delivery_app.domain.user.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.delivery_app.common.jwt.JwtTokenProvider;
import com.example.delivery_app.common.redis.dto.TokenRefreshRequest;
import com.example.delivery_app.common.redis.dto.TokenRefreshResponse;
import com.example.delivery_app.common.redis.service.RefreshTokenService;
import com.example.delivery_app.domain.user.dto.request.LoginRequest;
import com.example.delivery_app.domain.user.dto.request.SignUpRequest;
import com.example.delivery_app.domain.user.dto.response.LoginResponse;
import com.example.delivery_app.domain.user.entity.User;
import com.example.delivery_app.domain.user.entity.UserRole;
import com.example.delivery_app.domain.user.repository.UserRepository;

import io.jsonwebtoken.Claims;
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

	@Value("${spring.jwt.token.refresh.hour}")
	private long refreshTokenExpireHour;

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
	public TokenRefreshResponse reissue(TokenRefreshRequest refreshRequest) {

		log.info("[리프레시 요청] refreshRequest = {}", refreshRequest);
		log.info("[리프레시 요청] refreshToken = {}", refreshRequest.getRefreshToken());

		String refreshToken = refreshRequest.getRefreshToken();

		if (refreshToken == null || refreshToken.isBlank()) {
			throw new IllegalArgumentException("RefreshToken이 비여 있거나, null입니다.");
		}

		if(!jwtTokenProvider.validateToken(refreshToken)) {
			throw new IllegalArgumentException("유효하지 않은 RefreshToken 입니다.");
		}

		Claims claims = jwtTokenProvider.getClaims(refreshToken);

		Long userId = Long.parseLong(claims.getSubject());
		log.info("[리프레시 요청] 추출된 userId = {}", userId);

		String storedToken = refreshTokenService.get(userId);
		log.info("[리프레시 요청] Redis에서 가져온 refreshToken = {}", storedToken);

		if (storedToken == null || !storedToken.equals(refreshToken)) {
			throw new IllegalArgumentException("서버에 저장된 RefreshToken 과 다릅니다.");
		}

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		UserRole role = user.getRole();
		if (role == null) {
			throw new IllegalStateException("user.getRole()이 null입니다. DB 확인 필요!");
		}

		String newAccessToken = jwtTokenProvider.generateAccessToken(user.getId(), List.of(user.getRole()));



		String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId());
		long refreshTokenExpireTime = refreshTokenExpireHour * 60 * 60 * 1000;
		refreshTokenService.save(userId, newRefreshToken, refreshTokenExpireTime);

		return new TokenRefreshResponse(newAccessToken, newRefreshToken);
	}
}
