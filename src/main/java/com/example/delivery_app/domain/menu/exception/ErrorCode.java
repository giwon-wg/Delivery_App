package com.example.delivery_app.domain.menu.exception;

import org.springframework.http.HttpStatus;

import com.example.delivery_app.common.dto.ResponseCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode implements ResponseCode {

	// Menu 관련
	MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "찾는 메뉴가 없습니다."),
	MISMATCH_ERROR(HttpStatus.BAD_REQUEST, "가게와 메뉴가 일치하지 않습니다."),

	// MenuOption 관련
	MENU_OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "찾는 옵션이 없습니다."),
	MENU_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "이미 삭제된 옵션입니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
