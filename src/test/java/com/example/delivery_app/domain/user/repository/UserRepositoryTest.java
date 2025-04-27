package com.example.delivery_app.domain.user.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import com.example.delivery_app.common.exception.CustomException;
import com.example.delivery_app.domain.user.entity.User;
import com.example.delivery_app.domain.user.entity.UserRole;
import com.example.delivery_app.domain.user.exception.UserErrorCode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@Test
	@DisplayName("existsByEmailAndIsDeletedFalse: 이메일 중복 체크 성공")
	void existsByEmailAndIsDeletedFalse_True() {
		// given
		User user = User.builder()
			.email("test@example.com")
			.password("password")
			.nickname("tester")
			.role(UserRole.USER)
			.address("address")
			.isDeleted(false)
			.build();
		userRepository.save(user);

		// when
		boolean exists = userRepository.existsByEmailAndIsDeletedFalse("test@example.com");

		// then
		assertThat(exists).isTrue();
	}

	@Test
	@DisplayName("existsByNicknameAndIsDeletedFalse: 닉네임 중복 체크 성공")
	void existsByNicknameAndIsDeletedFalse_True() {
		// given
		User user = User.builder()
			.email("another@example.com")
			.password("password")
			.nickname("nick")
			.role(UserRole.USER)
			.address("address")
			.isDeleted(false)
			.build();
		userRepository.save(user);

		// when
		boolean exists = userRepository.existsByNicknameAndIsDeletedFalse("nick");

		// then
		assertThat(exists).isTrue();
	}

	@Test
	@DisplayName("findByEmail: 이메일로 유저 찾기 성공")
	void findByEmail_Success() {
		// given
		User user = User.builder()
			.email("findme@example.com")
			.password("password")
			.nickname("findnick")
			.role(UserRole.USER)
			.address("address")
			.isDeleted(false)
			.build();
		userRepository.save(user);

		// when
		Optional<User> found = userRepository.findByEmail("findme@example.com");

		// then
		assertThat(found).isPresent();
		assertThat(found.get().getEmail()).isEqualTo("findme@example.com");
	}

	@Test
	@DisplayName("findActiveById: 활성 유저 찾기 성공")
	void findActiveById_Success() {
		// given
		User user = User.builder()
			.email("active@example.com")
			.password("password")
			.nickname("active")
			.role(UserRole.USER)
			.address("address")
			.isDeleted(false)
			.build();
		User savedUser = userRepository.save(user);

		// when
		Optional<User> found = userRepository.findActiveById(savedUser.getId());

		// then
		assertThat(found).isPresent();
	}

	@Test
	@DisplayName("findByIdOrElseThrow: 없는 ID 조회시 예외 발생")
	void findByIdOrElseThrow_Fail() {
		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> userRepository.findByIdOrElseThrow(9999L));

		assertThat(exception.getResponseCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
	}
}