package com.example.delivery_app.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.delivery_app.domain.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByEmail(String email); // 이메일 중복 체크

	Optional<User> findByEmail(String email); // 이메일 로그인
}
