package com.example.delivery_app.common.redis.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlackListService {

	private final RedisTemplate<String, String> redisTemplate;

	public void addToBlacklist(String accessToken, long remainingMillis) {
		redisTemplate.opsForValue()
			.set("BL:" + accessToken, "logout", remainingMillis, TimeUnit.MILLISECONDS);
	}

	public boolean isBlacklisted(String accessToken) {
		return Boolean.TRUE.equals(redisTemplate.hasKey("BL:" + accessToken));
	}
}
