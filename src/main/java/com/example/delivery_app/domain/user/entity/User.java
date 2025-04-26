package com.example.delivery_app.domain.user.entity;

import com.example.delivery_app.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false, unique = true)
	private String nickname;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserRole role;

	@Column(nullable = false)
	private String address;

	@Column(nullable = false)
	private boolean isDeleted = false;

	@Builder
	public User(String email, String password, String nickname, UserRole role, String address, boolean isDeleted) {
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.role = role;
		this.address = address;
		this.isDeleted = isDeleted;
	}

	public void updateProfile(String nickname, String address) {
		this.nickname = nickname;
		if (address != null && !address.isBlank()) {
			this.address = address;
		}
	}

	public void changePassword(String encodedPassword) {
		this.password = encodedPassword;
	}

	public void delete() {
		this.isDeleted = true;
	}

	public void changeRole(UserRole newRole) {
		this.role = newRole;
	}
}
