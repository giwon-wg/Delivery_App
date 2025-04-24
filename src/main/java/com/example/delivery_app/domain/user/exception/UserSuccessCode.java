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
	DELETED(HttpStatus.OK, "요청이 성공적으로 삭제되었습니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
