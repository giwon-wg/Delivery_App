package com.example.delivery_app.domain.menu.dto.requestdto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateMenuRequestDto {

	@JsonProperty(value = "menuPicture")
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

	public UpdateMenuRequestDto(String menuPicture, String menuName, Integer price, String menuContent) {
		this.menuPicture = menuPicture;
		this.menuName = menuName;
		this.price = price;
		this.menuContent = menuContent;
	}
}
