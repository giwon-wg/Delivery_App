package com.example.delivery_app.domain.user.Auth;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.example.delivery_app.domain.user.entity.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAuth {
	private Long id;
	private List<UserRole> roles;

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles.stream()
			.map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
			.toList();
	}

	public boolean hasRole(String roleName) {
		if (roles == null) return false;
		return roles.stream()
			.anyMatch(role -> role.name().equalsIgnoreCase(roleName));
	}

	/**
	 * 테스트용으로 작성
	 */
	public static UserAuth of(Long id, List<UserRole> roles) {
		return new UserAuth(id, roles);
	}

}
