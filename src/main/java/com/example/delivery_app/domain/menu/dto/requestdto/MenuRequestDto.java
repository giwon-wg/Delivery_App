package com.example.delivery_app.domain.menu.dto.requestdto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MenuRequestDto {

	@NotBlank(message = "카테고리를 입력해주세요")
	@JsonProperty("category")
	private final String category;

	@JsonProperty("menuPicture")
	private final String menuPicture;

	@NotBlank(message = "메뉴명을 입력해주세요")
	@Size(max = 20, message = "메뉴명은 20자 이내로 입력해주세요")
	@JsonProperty("menuName")
	private final String menuName;

	@NotNull(message = "가격을 입력해주세요")
	@JsonProperty("price")
	private final Integer price;

	@NotBlank(message = "메뉴 설명을 입력해주세요")
	@JsonProperty("menuContent")
	private final String menuContent;

}
