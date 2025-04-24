package com.example.delivery_app.domain.menu.dto.responsedto;

import com.example.delivery_app.domain.menu.entity.MenuOption;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class MenuOptionResponseDto {

	private final Long menuId;

	private final Long id;

	private final String optionName;

	private final Integer price;

	private final String content;

	public static MenuOptionResponseDto fromMenu(MenuOption menuOption) {
		return MenuOptionResponseDto.builder()
			.menuId(menuOption.getMenu().getId())
			.id(menuOption.getId())
			.optionName(menuOption.getOptionName())
			.price(menuOption.getPrice())
			.content(menuOption.getContent())
			.build();
	}
}
