package com.example.delivery_app.common.jwt;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.delivery_app.domain.user.entity.UserRole;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Component
public class JwtTokenProvider {

	private final Key key;
	private final long accessTokenExpireTime;
	private final long refreshTokenExpireTime;

	public JwtTokenProvider(
		@Value("${spring.jwt.secret}") String secretKey,
		@Value("${spring.jwt.token.access.minute}") long accessHour,
		@Value("${spring.jwt.token.refresh.minute}") long refreshHour
	) {
		this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
		this.accessTokenExpireTime = 1000 * 60 * accessHour; // 15분
		this.refreshTokenExpireTime =  1000 * 60 * refreshHour; // 14일
	}

	public String generateAccessToken(Long userId, List<UserRole> roles) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + accessTokenExpireTime);

		return Jwts.builder()
			.setSubject(userId.toString())
			.claim("roles", roles.stream()
				.map(UserRole::name)
				.collect(Collectors.toList()))
			.setIssuedAt(now)
			.setExpiration(expiry)
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	public String generateRefreshToken(Long userId) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + refreshTokenExpireTime);

		return Jwts.builder()
			.setSubject(userId.toString())
			.setIssuedAt(now)
			.setExpiration(expiry)
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public Claims getClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build()
			.parseClaimsJws(token)
			.getBody();
	}

	public Date getExpiration(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token)
			.getBody()
			.getExpiration();
	}

	public String resolveAccessTokenFromContext(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}
}
