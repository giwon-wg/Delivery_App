package com.example.delivery_app.domain.menu.dto.responsedto;

import com.example.delivery_app.domain.menu.entity.Menu;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class MenuResponseDto {

	private final String category;

	private final String menuPicture;

	private final String menuName;

	private final int price;

	private final String menuContent;

	public static MenuResponseDto fromMenu(Menu menu) {
		return MenuResponseDto.builder()
			.category(menu.getCategory())
			.menuPicture(menu.getMenuPicture())
			.menuName(menu.getMenuName())
			.price(menu.getPrice())
			.menuContent(menu.getMenuContent())
			.build();
	}
}
