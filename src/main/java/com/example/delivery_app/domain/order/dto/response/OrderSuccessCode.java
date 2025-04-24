package com.example.delivery_app.domain.order.dto.response;

import org.springframework.http.HttpStatus;

import com.example.delivery_app.common.dto.ResponseCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OrderSuccessCode implements ResponseCode {
	ORDER_GET_ALL_SUCCESS(HttpStatus.OK, "주문 내역 리스트를 성공적으로 조회하였습니다."),
	ORDER_GET_DETAIL_SUCCESS(HttpStatus.OK, "주문 상세 정보를 성공적으로 조회하였습니다."),
	ORDER_CREATE_SUCCESS(HttpStatus.CREATED, "주문이 성공적으로 요청되었습니다."),
	ORDER_ACCEPT_SUCCESS(HttpStatus.OK, "주문이 수락되었습니다."),
	ORDER_COMPLETE_SUCCESS(HttpStatus.OK, "배달이 완료되었습니다."),
	ORDER_REJECT_SUCCESS(HttpStatus.OK, "주문이 거절되었습니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
