package com.example.delivery_app.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.delivery_app.common.dto.ResponseCode;
import com.example.delivery_app.common.dto.ResponseDto;

@RestControllerAdvice
public class GlobalExceptionHandler {
	/**
	 * 커스텀 예외 처리
	 */
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ResponseDto<Void>> handleCustomException(CustomException exception) {
		ResponseCode responseCode = exception.getResponseCode();

		// 예외 응답 DTO 생성
		ResponseDto<Void> responseDto = ResponseDto.of(
			responseCode
		);
		return ResponseEntity.status(responseCode.getHttpStatus()).body(responseDto);
	}
	// FIXME: 추후 디테일한 예외처리 할 예정
}
