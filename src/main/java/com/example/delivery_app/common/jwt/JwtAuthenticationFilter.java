package com.example.delivery_app.common.jwt;

import com.example.delivery_app.domain.user.Auth.UserAuth;
import com.example.delivery_app.domain.user.entity.UserRole;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String token = resolveToken(request);

		if (token != null && jwtTokenProvider.validateToken(token)) {
			Claims claims = jwtTokenProvider.getClaims(token);
			Long userId = Long.parseLong(claims.getSubject());

			// roles 꺼내기 (List<String> → List<UserRole>)
			List<String> roleNames = claims.get("roles", List.class);

			if (roleNames == null) {
				log.warn("JWT 토큰에 roles 클레임이 없습니다. userId: {}", userId);
				roleNames = List.of(); // 또는 throw new CustomAuthenticationException("권한 없음");
			}

			List<UserRole> roles = roleNames.stream()
				.map(UserRole::valueOf)
				.toList();

			// UserAuth 객체 생성
			UserAuth userAuth = UserAuth.builder()
				.id(userId)
				.roles(roles)
				.build();

			// 인증 객체 등록
			UsernamePasswordAuthenticationToken authentication =
				new UsernamePasswordAuthenticationToken(
					userAuth,
					null,
					userAuth.getAuthorities()
				);
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		filterChain.doFilter(request, response);
	}

	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}
}
