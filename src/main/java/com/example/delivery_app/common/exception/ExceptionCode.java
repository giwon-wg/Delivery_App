package com.example.delivery_app.common.exception;

import org.springframework.http.HttpStatus;

import com.example.delivery_app.common.dto.ResponseCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode implements ResponseCode {
	INVALID_PATH_VARIABLE(HttpStatus.BAD_REQUEST, "경로 변수를 정확하게 입력해주세요."),
	INVALID_INPUT_FIELD(HttpStatus.BAD_REQUEST, "잘못 입력한 필드값이 존재합니다."),
	INVALID_INPUT_PARSING(HttpStatus.BAD_REQUEST, "JSON 파싱이 제대로 되지 않았습니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러입니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
