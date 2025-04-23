package com.example.delivery_app.common.redis.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

	private final RedisTemplate<String, String> redisTemplate;

	public void save(Long userId, String refreshToken, Long expireMs) {
		redisTemplate.opsForValue().set("RT: " + userId, refreshToken, expireMs, TimeUnit.MILLISECONDS);
	}

	public String get(Long userId) {
		return redisTemplate.opsForValue().get("RT:" + userId);
	}

	public void delete(Long userId) {
		redisTemplate.delete("RT:" + userId);
	}
}
