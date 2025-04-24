package com.example.delivery_app.domain.menu.dto.responsedto;

import com.example.delivery_app.domain.menu.entity.Menu;

import lombok.Getter;

@Getter
public class DeleteResponseDto {

	private final Long id;

	private final String menuName;

	public DeleteResponseDto(Menu menu) {
		this.id = menu.getId();
		this.menuName = menu.getMenuName();
	}
}
