package com.example.delivery_app.common.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@JsonPropertyOrder({"timestamp", "code", "message", "data"})
public class CommonResponseDto<T> {
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private final LocalDateTime timestamp;
	private final int code;
	private final String message;

	@JsonInclude(JsonInclude.Include.NON_NULL) // ✅ null 이면 응답 JSON 에서 생략됨
	private final T data;

	// 생성자 (정적 팩토리 메서드 패턴 적용을 위한 private 설정)
	private CommonResponseDto(ResponseCode responseCode, T data) {
		this.timestamp = LocalDateTime.now();
		this.code = responseCode.getHttpStatus().value();
		this.message = responseCode.getMessage();
		this.data = data;
	}

	/**
	 * 정적 팩토리메서드 패턴의 생성자
	 * @param responseCode 도메인별 응답코드 객체
	 * @param data 도메인별 응답 DTO
	 * @return ResponseDto 반환
	 */
	public static <T> CommonResponseDto<T> of(ResponseCode responseCode, T data) {
		return new CommonResponseDto<>(responseCode, data);
	}

	/**
	 * 정적 팩토리메서드 패턴의 생성자
	 * ✅응답할 데이터가 없을 때 사용합니다!
	 * @param responseCode 도메인별 응답 코드 객체
	 * @return 도메인별 응답 DTO
	 */
	public static <T> CommonResponseDto<T> of(ResponseCode responseCode) {
		return new CommonResponseDto<>(responseCode, null);
	}
}
