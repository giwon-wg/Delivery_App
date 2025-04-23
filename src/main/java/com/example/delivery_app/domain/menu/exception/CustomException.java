package com.example.delivery_app.domain.menu.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

	private final ErrorCode errorCode;

	public CustomException(ErrorCode errorCode) {
		super(ErrorCode.MENU_NOT_FOUND.getMessage());
		this.errorCode = errorCode;
	}
}
