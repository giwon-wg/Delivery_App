package com.example.delivery_app.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.delivery_app.common.dto.CommonResponseDto;
import com.example.delivery_app.common.dto.ResponseCode;

@RestControllerAdvice
public class GlobalExceptionHandler {
	/**
	 * 커스텀 예외 처리
	 */
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<CommonResponseDto<Void>> handleCustomException(CustomException exception) {
		ResponseCode responseCode = exception.getResponseCode();

		// 예외 응답 DTO 생성
		CommonResponseDto<Void> commonResponseDto = CommonResponseDto.of(
			responseCode
		);
		return ResponseEntity.status(responseCode.getHttpStatus()).body(commonResponseDto);
	}
	// FIXME: 추후 디테일한 예외처리 할 예정
}
