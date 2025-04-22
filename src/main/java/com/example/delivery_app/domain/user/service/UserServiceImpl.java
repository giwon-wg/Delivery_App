package com.example.delivery_app.domain.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.delivery_app.common.jwt.JwtTokenProvider;
import com.example.delivery_app.domain.user.dto.request.LoginRequest;
import com.example.delivery_app.domain.user.dto.request.SignUpRequest;
import com.example.delivery_app.domain.user.dto.response.LoginResponse;
import com.example.delivery_app.domain.user.entity.User;
import com.example.delivery_app.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;

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

		String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getRole());
		String refreshToken = jwtTokenProvider.generateRefreshToken();

		return new LoginResponse(accessToken, refreshToken);
	}
}
