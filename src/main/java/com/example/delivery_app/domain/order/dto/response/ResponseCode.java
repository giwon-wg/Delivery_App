package com.example.delivery_app.domain.order.dto.response;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ResponseCode {
	ORDER_GET_LIST_SUCCESS(HttpStatus.OK, "주문 내역 리스트를 성공적으로 조회하였습니다."),
	ORDER_GET_DETAIL_SUCCESS(HttpStatus.OK, "주문 상세 정보를 성공적으로 조회하였습니다."),
	ORDER_POST_CREATE_SUCCESS(HttpStatus.CREATED, "주문이 성공적으로 요청되었습니다."),
	ORDER_PATCH_ACCEPTED_SUCCESS(HttpStatus.OK, "주문이 수락되었습니다."),
	ORDER_PATCH_COMPLETED_SUCCESS(HttpStatus.OK, "배달이 완료되었습니다."),
	ORDER_PATCH_REJECTED_SUCCESS(HttpStatus.OK, "주문이 거절되었습니다.");

	private final HttpStatus status;
	private final String message;
}
