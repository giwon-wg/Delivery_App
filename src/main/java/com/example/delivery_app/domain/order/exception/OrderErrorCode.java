package com.example.delivery_app.domain.order.exception;

import org.springframework.http.HttpStatus;

import com.example.delivery_app.common.dto.ResponseCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OrderErrorCode implements ResponseCode {
	ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문 정보를 찾을 수 없습니다."),
	ORDER_FORBIDDEN(HttpStatus.FORBIDDEN, "주문에 권한이 없습니다."),

	ORDER_STATUS_ALREADY_ACCEPTED(HttpStatus.BAD_REQUEST, "이미 배달중인 주문입니다."),
	ORDER_STATUS_NOT_ACCEPTED(HttpStatus.BAD_REQUEST, "수락되지 않은 주문은 배달할 수 없습니다."),
	ORDER_STATUS_INVALID(HttpStatus.BAD_REQUEST, "배달완료 되었거나 취소된 주문은 상태변경을 할 수 없습니다."),

	ORDER_STATUS_EQUALS(HttpStatus.BAD_REQUEST, "중복된 요청입니다."),

	ORDER_INVALID_STORE(HttpStatus.BAD_REQUEST, "마감했거나 폐업한 가게입니다."),
	ORDER_INVALID_MIN_PRICE(HttpStatus.BAD_REQUEST, "");

	private final HttpStatus httpStatus;
	private final String message;
}
