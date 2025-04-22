package com.example.delivery_app.domain.menu.dto.requestdto;

import com.example.delivery_app.domain.menu.entity.Menu;

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

	public MenuRequestDto(Menu menu) {
		this.category = menu.getCategory();
		this.menuPicture = menu.getMenuPicture();
		this.menuName = menu.getMenuName();
		this.price = menu.getPrice();
		this.menuContent = menu.getMenuContent();
	}

	public static MenuRequestDto toEntity(Menu menu) {
		return new MenuRequestDto(menu);
	}
}
