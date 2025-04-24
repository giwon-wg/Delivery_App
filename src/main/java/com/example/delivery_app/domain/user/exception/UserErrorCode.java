package com.example.delivery_app.domain.user.exception;

import org.springframework.http.HttpStatus;

import com.example.delivery_app.common.dto.ResponseCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserErrorCode implements ResponseCode {

	// 400
	DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "이미 사용 중인 이메일입니다."),
	DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "이미 사용 중인 닉네임입니다."),
	PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "현재 비밀번호가 일치하지 않습니다."),
	PASSWORD_CONFIRM_MISMATCH(HttpStatus.BAD_REQUEST, "새 비밀번호와 비밀번호 확인이 일치하지 않습니다."),


	// 403
	ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
	ADMIN_SELF_DELETE_BLOCKED(HttpStatus.FORBIDDEN, "어드민은 본인을 삭제할 수 없습니다."),

	// 404
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
	EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "이메일에 해당하는 유저가 없습니다."),

	// 500
	USER_ROLE_NOT_DEFINED(HttpStatus.INTERNAL_SERVER_ERROR, "유저 역할이 정의되지 않았습니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
