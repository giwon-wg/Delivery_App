package com.example.delivery_app.domain.menu.dto.responsedto;

import com.example.delivery_app.domain.menu.entity.Menu;

import lombok.Getter;

@Getter
public class MenuResponseDto {

	private final String category;

	private final String menuPicture;

	private final String menuName;

	private final int price;

	private final String menuContent;

	public MenuResponseDto(Menu menu) {
		this.category = menu.getCategory();
		this.menuPicture = menu.getMenuPicture();
		this.menuName = menu.getMenuName();
		this.price = menu.getPrice();
		this.menuContent = menu.getMenuContent();
	}

	public static MenuResponseDto toDto(Menu menu) {
		return new MenuResponseDto(menu);
	}
}
