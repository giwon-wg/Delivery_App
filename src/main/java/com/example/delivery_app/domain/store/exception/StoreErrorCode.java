package com.example.delivery_app.domain.store.exception;

import org.springframework.http.HttpStatus;

import com.example.delivery_app.common.dto.ResponseCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * StoreErrorCode 열거형은 가게 관련 비즈니스 예외 상황에 대응하기 위한 에러 코드를 정의합니다.
 * 각 에러 코드는 {@link HttpStatus} 와 사용자에게 제공될 메시지를 함께 포함합니다.
 */
@Getter
@RequiredArgsConstructor
public enum StoreErrorCode implements ResponseCode {

	/**
	 * 존재하지 않는 가게를 조회하거나 수정하려고 할 때
	 */
	STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 가게가 존재하지 않습니다."),

	/**
	 * 유저가 생성할 수 있는 가게 수(3개)를 초과하려 할 때
	 */
	STORE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "운영 가능한 가게는 최대 3개까지만 등록할 수 있습니다."),

	/**
	 * 연관된 유저가 존재하지 않을 때
	 */
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저가 존재하지 않습니다."),

	/**
	 * 요청한 유저가 해당 가게의 소유주가 아닐 때
	 */
	NOT_STORE_OWNER(HttpStatus.FORBIDDEN, "해당 가게의 소유주가 아닙니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
