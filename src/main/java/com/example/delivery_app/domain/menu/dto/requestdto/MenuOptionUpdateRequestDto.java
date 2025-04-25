package com.example.delivery_app.domain.menu.dto.requestdto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MenuOptionUpdateRequestDto {

	private final String optionName;

	private final Integer price;

	private final String content;

}
