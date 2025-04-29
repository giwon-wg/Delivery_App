package com.example.delivery_app.domain.user.dto.response;

import com.example.delivery_app.domain.user.entity.User;
import com.example.delivery_app.domain.user.entity.UserRole;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserProfileDto {
	private Long id;

	private String nickname;

	private String email;

	private String address;

	private UserRole role;

	public static UserProfileDto fromPrivate(User user) {
		return new UserProfileDto(
			user.getId(),
			user.getNickname(),
			user.getEmail(),
			user.getAddress(),
			user.getRole()
		);
	}

	public static UserProfileDto fromPublic(User user) {
		return new UserProfileDto(
			user.getId(),
			user.getNickname(),
			null,
			null,
			user.getRole()
		);
	}
}
