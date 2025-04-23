package com.example.delivery_app.domain.menu.dto.requestdto;

import com.example.delivery_app.domain.menu.entity.Menu;

import lombok.Getter;

@Getter
public class UpdateMenuRequestDto {

	private final String menuPicture;

	private final String menuName;

	private final Integer price;

	private final String menuContent;

	public UpdateMenuRequestDto(Menu menu) {
		this.menuPicture = menu.getMenuPicture();
		this.menuName = menu.getMenuName();
		this.price = menu.getPrice();
		this.menuContent = menu.getMenuContent();
	}
}
