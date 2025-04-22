package com.example.delivery_app.domain.order.dto.response;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SuccessResponseDto<T> {
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private final LocalDateTime timestamp;

	private final int code;
	private final String path;
	private final String message;

	@JsonInclude(JsonInclude.Include.NON_NULL) // ✅ null 이면 응답 JSON 에서 생략됨
	private final T data;

	/**
	 * 공통 응답 객체(SuccessResponseDto)를 ResponseEntity 객체로 감싸서 반환하는 메서드
	 * Controller 에서 응답 객체를 공통화하기 위해서 사용됩니다.
	 * @param responseCode 성공 응답 enum
	 * @param path 요청 URI (ex: /api/orders)
	 * @param data 응답할 제네릭 데이터
	 * @return ResponseEntity 를 반환
	 */
	public static <T> ResponseEntity<SuccessResponseDto<T>> createResponseEntityDto(
		ResponseCode responseCode,
		String path,
		T data
	) {
		SuccessResponseDto<T> dto = new SuccessResponseDto<>(
			LocalDateTime.now(),
			responseCode.getStatus().value(),
			path,
			responseCode.getMessage(),
			data);
		return ResponseEntity.status(responseCode.getStatus()).body(dto);
	}
}
