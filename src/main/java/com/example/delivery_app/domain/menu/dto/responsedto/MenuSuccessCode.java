package com.example.delivery_app.domain.menu.dto.responsedto;

import org.springframework.http.HttpStatus;

import com.example.delivery_app.common.dto.ResponseCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MenuSuccessCode implements ResponseCode {
	MENU_CREATE_SUCCESS(HttpStatus.CREATED, "메뉴가 정상적으로 작성되었습니다."),
	MENU_PATCH_SUCCESS(HttpStatus.OK, "메뉴가 정상적으로 수정되었습니다."),
	MENU_DELETE_SUCCESS(HttpStatus.OK, "메뉴가 정상적으로 삭제되었습니다."),
	MENU_GET_SUCCESS(HttpStatus.OK, "메뉴가 정상적으로 조회되었습니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
