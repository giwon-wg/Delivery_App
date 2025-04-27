package com.example.delivery_app.domain.user.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UserTest {
	@Test
	@DisplayName("updateProfile: 닉네임과 주소가 정상적으로 업데이트된다")
	void updateProfile() {
		// given
		User user = createUser();

		// when
		user.updateProfile("newNickname", "newAddress");

		// then
		assertThat(user.getNickname()).isEqualTo("newNickname");
		assertThat(user.getAddress()).isEqualTo("newAddress");
	}

	@Test
	@DisplayName("changePassword: 비밀번호가 정상적으로 변경된다")
	void changePassword() {
		// given
		User user = createUser();

		// when
		user.changePassword("newPassword");

		// then
		assertThat(user.getPassword()).isEqualTo("newPassword");
	}

	@Test
	@DisplayName("delete: isDeleted가 true로 변경된다")
	void delete() {
		// given
		User user = createUser();

		// when
		user.delete();

		// then
		assertThat(user.isDeleted()).isTrue();
	}

	@Test
	@DisplayName("changeRole: 역할이 정상적으로 변경된다")
	void changeRole() {
		// given
		User user = createUser();

		// when
		user.changeRole(UserRole.ADMIN);

		// then
		assertThat(user.getRole()).isEqualTo(UserRole.ADMIN);
	}

	private User createUser() {
		return User.builder()
			.email("test@example.com")
			.password("password")
			.nickname("nickname")
			.role(UserRole.USER)
			.address("address")
			.isDeleted(false)
			.build();
	}
}
