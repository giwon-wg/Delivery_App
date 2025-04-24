package com.example.delivery_app.domain.menu.dto.requestdto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MenuOptionRequestDto {

	@JsonProperty("optionName")
	private final String optionName;

	@JsonProperty("price")
	private final Integer price;

	@JsonProperty("content")
	private final String content;

}
