package com.example.delivery_app.domain.menu.dto.responsedto;

import com.example.delivery_app.domain.menu.entity.Menu;

import lombok.Getter;

@Getter
public class MenuCreateResponseDto {

	private final Long storeId;

	private final Long id;

	private final String category;

	private final String menuPicture;

	private final String menuName;

	private final int price;

	private final String menuContent;

	private MenuCreateResponseDto(Menu menu) {
		this.storeId = menu.getStore().getStoreId();
		this.id = menu.getId();
		this.category = menu.getCategory();
		this.menuPicture = menu.getMenuPicture();
		this.menuName = menu.getMenuName();
		this.price = menu.getPrice();
		this.menuContent = menu.getMenuContent();
	}

	public static MenuCreateResponseDto menuFrom(Menu menu) {
		return new MenuCreateResponseDto(menu);
	}
}
