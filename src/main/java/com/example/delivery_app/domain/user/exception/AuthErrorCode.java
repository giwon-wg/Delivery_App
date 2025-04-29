package com.example.delivery_app.domain.user.exception;

import org.springframework.http.HttpStatus;

import com.example.delivery_app.common.dto.ResponseCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AuthErrorCode implements ResponseCode {

	// === 400 BAD_REQUEST ===
	NULL_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "RefreshToken 이 비어 있습니다."),
	INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 RefreshToken 입니다."),
	TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "토큰이 만료되었습니다."),
	TOKEN_INVALID(HttpStatus.BAD_REQUEST, "유효하지 않은 토큰입니다."),
	TOKEN_MISMATCH(HttpStatus.BAD_REQUEST, "저장된 토큰과 일치하지 않습니다."),

	// === 401 UNAUTHORIZED ===
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
	UNAUTHENTICATED_USER(HttpStatus.UNAUTHORIZED, "로그인된 사용자가 아닙니다."),
	AUTH_HEADER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "Authorization 헤더가 존재하지 않습니다."),
	AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "사용자 인증에 실패했습니다."),

	// === 403 FORBIDDEN ===
	ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
