package com.example.delivery_app.domain.menu.dto.responsedto;

import com.example.delivery_app.domain.menu.entity.Menu;

import lombok.Getter;

@Getter
public class UpdateMenuResponseDto {
	
	private final Long id;

	private final String category;

	private final String menuPicture;

	private final String menuName;

	private final Integer price;

	private final String menuContent;

	public UpdateMenuResponseDto(Menu menu) {
		this.id = menu.getId();
		this.category = menu.getCategory();
		this.menuPicture = menu.getMenuPicture();
		this.menuName = menu.getMenuName();
		this.price = menu.getPrice();
		this.menuContent = menu.getMenuContent();
	}
}
