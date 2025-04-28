package com.example.delivery_app.domain.menu.dto.responsedto;

import java.util.List;
import java.util.stream.Collectors;

import com.example.delivery_app.domain.menu.entity.Menu;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
@EqualsAndHashCode // 객체의 주소값이 아닌 값들을 비교할 수 있게 해주는 어노테이션
public class MenuResponseDto {

	private final Long storeId;

	private final Long id;

	private final String category;

	private final String menuPicture;

	private final String menuName;

	private final int price;

	private final String menuContent;

	private final List<MenuOptionResponseDto> menuOptions;

	public static MenuResponseDto fromMenu(Menu menu) {
		return MenuResponseDto.builder()
			.storeId(menu.getStore().getStoreId())
			.id(menu.getId())
			.category(menu.getCategory())
			.menuPicture(menu.getMenuPicture())
			.menuName(menu.getMenuName())
			.price(menu.getPrice())
			.menuContent(menu.getMenuContent())
			.menuOptions(menu.getMenuOptions().stream()
				.filter(menuOption -> !menuOption.isDeleted())
				.map(MenuOptionResponseDto::fromMenuOption)
				.collect(Collectors.toList()))
			.build();
	}
}
