package com.example.delivery_app.domain.menu.dto.requestdto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class MenuRequestDto {

	@NotBlank(message = "카테고리를 입력해주세요")
	private final String category;

	private final String menuPicture;

	@NotBlank(message = "메뉴명을 입력해주세요")
	@Size(max = 20, message = "메뉴명은 20자 이내로 입력해주세요")
	private final String menuName;

	@NotNull(message = "가격을 입력해주세요")
	private final Integer price;

	@NotBlank(message = "메뉴 설명을 입력해주세요")
	private final String menuContent;

	public MenuRequestDto(String category, String menuPicture, String menuName, Integer price, String menuContent) {
		this.category = category;
		this.menuPicture = menuPicture;
		this.menuName = menuName;
		this.price = price;
		this.menuContent = menuContent;
	}
}
