package com.example.delivery_app.common.exception;

import com.example.delivery_app.common.dto.ResponseCode;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
	private final ResponseCode responseCode;

	public CustomException(ResponseCode responseCode) {
		super(responseCode.getMessage()); // RuntimeException 초기화
		this.responseCode = responseCode;
	}
}
