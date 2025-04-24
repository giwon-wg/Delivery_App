package com.example.delivery_app.domain.store.exception;

import org.springframework.http.HttpStatus;

import com.example.delivery_app.common.dto.ResponseCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StoreErrorCode implements ResponseCode {
	STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 가게가 존재하지 않습니다."),
	STORE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "운영 가능한 가게는 최대 3개까지만 등록할 수 있습니다."),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저가 존재하지 않습니다."),
	NOT_STORE_OWNER(HttpStatus.FORBIDDEN, "해당 가게의 소유주가 아닙니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
