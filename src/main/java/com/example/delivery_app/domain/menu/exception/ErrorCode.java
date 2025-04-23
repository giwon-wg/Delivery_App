package com.example.delivery_app.domain.menu.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	MENU_NOT_FOUND(404, "Not Found", "M001", "찾는 메뉴가 없습니다.");

	private final int status;
	private final String error;
	private final String code;
	private final String message;
}
