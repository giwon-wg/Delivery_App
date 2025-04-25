package com.example.delivery_app.domain.menu.dto.responsedto;

import com.example.delivery_app.domain.menu.entity.MenuOption;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class MenuOptionDeleteResponseDto {

	private final Long storeId;

	private final Long menuId;

	private final Long id;

	private final String optionName;

	private final String isDeleted;

	public static MenuOptionDeleteResponseDto fromMenuOption(MenuOption menuOption) {
		return MenuOptionDeleteResponseDto.builder()
			.storeId(menuOption.getMenu().getStore().getStoreId())
			.menuId(menuOption.getMenu().getId())
			.id(menuOption.getId())
			.optionName(menuOption.getOptionName())
			// .isDeleted(menuOption.getIs)
			.build();
	}
}
