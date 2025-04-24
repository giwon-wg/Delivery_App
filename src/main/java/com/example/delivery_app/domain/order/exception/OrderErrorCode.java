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

	ORDER_ACCEPT_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "이미 완료된 주문입니다."),
	ORDER_ACCEPT_ALREADY_ACCEPTED(HttpStatus.BAD_REQUEST, "이미 배달중인 주문입니다."),
	ORDER_ACCEPT_ALREADY_CANCELED(HttpStatus.BAD_REQUEST, "이미 취소된 주문입니다."),

	ORDER_CANCEL_ALREADY_ACCEPTED(HttpStatus.BAD_REQUEST, "배달중인 주문은 취소할 수 없습니다."),
	ORDER_CANCEL_NOT_ACCEPTED(HttpStatus.BAD_REQUEST, "수락되지 않은 주문은 취소할 수 없습니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
