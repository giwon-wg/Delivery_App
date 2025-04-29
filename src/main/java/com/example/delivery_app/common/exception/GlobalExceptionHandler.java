package com.example.delivery_app.common.exception;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
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

	/**
	 * 유효성 검증 실패 예외 처리 (ex. @Valid 검증 실패)
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<CommonResponseDto<String>> handleValidationException(MethodArgumentNotValidException ex) {
		String errorMessage = ex.getBindingResult().getFieldErrors()
			.stream()
			.map(error -> error.getField() + ": " + error.getDefaultMessage())
			.collect(Collectors.joining(", "));  // 여러 필드일 경우 ','로 구분

		CommonResponseDto<String> response = CommonResponseDto.of(
			ExceptionCode.INVALID_INPUT_FIELD, errorMessage
		);
		return ResponseEntity.status(ex.getStatusCode()).body(response);
	}

	/**
	 * 경로 변수 예외 처리 (ex. @PathVariable 누락)
	 */
	@ExceptionHandler(MissingPathVariableException.class)
	public ResponseEntity<CommonResponseDto<Void>> handleMissingPathVariableException(
		MissingPathVariableException ex) {
		CommonResponseDto<Void> responseDto = CommonResponseDto.of(ExceptionCode.INVALID_PATH_VARIABLE);
		return ResponseEntity.status(ex.getStatusCode()).body(responseDto);
	}

	/**
	 * JSON 파싱 실패 예외처리
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<CommonResponseDto<String>> handleJsonParseException(HttpMessageNotReadableException ex) {
		CommonResponseDto<String> responseDto = CommonResponseDto.of(
			ExceptionCode.INVALID_INPUT_PARSING, ex.getMessage()
		);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
	}

	/**
	 * 그 외의 경우 서버에러 예외처리
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<CommonResponseDto<Void>> handleException(Exception ex) {
		CommonResponseDto<Void> responseDto = CommonResponseDto.of(ExceptionCode.INTERNAL_SERVER_ERROR);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
	}
}
