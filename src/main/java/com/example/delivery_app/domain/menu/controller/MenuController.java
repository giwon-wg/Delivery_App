package com.example.delivery_app.domain.menu.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.delivery_app.domain.menu.dto.requestdto.MenuRequestDto;
import com.example.delivery_app.domain.menu.dto.responsedto.MenuResponseDto;
import com.example.delivery_app.domain.menu.service.MenuService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/store/{storeId}/menus")
public class MenuController {

	private final MenuService menuService;

	/**
	 * 메뉴 저장
	 * @param storeId
	 * @param dto
	 * @return
	 */
	@PostMapping
	public ResponseEntity<MenuResponseDto> saveMenu(
		@PathVariable Long storeId,
		@Valid @RequestBody MenuRequestDto dto
	) {

		MenuResponseDto savedMenu = menuService.saveMenu(storeId, dto);

		return new ResponseEntity<>(savedMenu, HttpStatus.CREATED);
	}

}
