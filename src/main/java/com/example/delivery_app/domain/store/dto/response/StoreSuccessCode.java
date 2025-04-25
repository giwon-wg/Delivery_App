package com.example.delivery_app.domain.store.dto.response;

import org.springframework.http.HttpStatus;

import com.example.delivery_app.common.dto.ResponseCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 가게(Store) 관련 API 요청이 성공했을 때 반환할 응답 코드(Enum)입니다.
 * 각 응답은 {@link HttpStatus}와 사용자에게 전달될 메시지를 포함합니다.
 */
@RequiredArgsConstructor
@Getter
public enum StoreSuccessCode implements ResponseCode {
	STORE_GET_ALL_SUCCESS(HttpStatus.OK, "전체 가게 리스트를 성공적으로 조회 했습니다."),
	STORE_GET_BY_ID_SUCCESS(HttpStatus.OK, "ID에 맞는 가게 정보를 성공적으로 조회 했습니다."),
	STORE_ADD_SUCCESS(HttpStatus.OK, "가게를 성공적으로 등록 했습니다."),
	STORE_UPDATE_SUCCESS(HttpStatus.OK, "가게 정보를 성공적으로 수정 했습니다."),
	STORE_INACTIVE_SUCCESS(HttpStatus.OK, "해당 가게를 성공적으로 폐업 처리 했습니다."),
	STORE_OP_TIME_UPDATE_SUCCESS(HttpStatus.OK, "가게의 영업시간 정보를 성공적으로 수정 했습니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
