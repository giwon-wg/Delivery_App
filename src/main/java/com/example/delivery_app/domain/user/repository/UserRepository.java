package com.example.delivery_app.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.delivery_app.domain.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByEmail(String email); // 이메일 중복 체크

	Optional<User> findByEmail(String email); // 이메일 로그인

	boolean existsByNickname(String nickname); // 닉네임 중복 체크

	default User findByIdOrElseThrow(Long id) {
		return findById(id)
			.orElseThrow(() -> new RuntimeException("추후 수정해야 합니다."));
	}
}
