package com.example.delivery_app.domain.user.exception;

import org.springframework.http.HttpStatus;

import com.example.delivery_app.common.dto.ResponseCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserSuccessCode implements ResponseCode {
	SUCCESS(HttpStatus.OK, "요청이 성공적으로 처리되었습니다."),
	CREATED(HttpStatus.CREATED, "요청이 성공적으로 등록되었습니다."),
	DELETED(HttpStatus.OK, "요청이 성공적으로 삭제되었습니다."),
	OWNER_GRANTED(HttpStatus.OK, "사업자 권한이 부여되었습니다. 다시 로그인해주세요.");

	private final HttpStatus httpStatus;
	private final String message;
}
