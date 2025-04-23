package com.example.delivery_app.domain.menu.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.delivery_app.domain.menu.dto.requestdto.MenuRequestDto;
import com.example.delivery_app.domain.menu.dto.requestdto.UpdateMenuRequestDto;
import com.example.delivery_app.domain.menu.dto.responsedto.MenuResponseDto;
import com.example.delivery_app.domain.menu.dto.responsedto.UpdateMenuResponseDto;
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

	/**
	 * 메뉴 수정
	 * @param storeId
	 * @param menuId
	 * @param dto
	 * @return
	 */
	@PatchMapping("/{menuId}")
	public ResponseEntity<UpdateMenuResponseDto> updateMenu(
		@PathVariable Long storeId,
		@PathVariable Long menuId,
		@Valid @RequestBody UpdateMenuRequestDto dto
	) {

		UpdateMenuResponseDto updateMenu = menuService.updateMenu(storeId, menuId, dto);

		return new ResponseEntity<>(updateMenu, HttpStatus.OK);
	}

	@DeleteMapping("/{menuId}")
	public ResponseEntity<Void> deleteMenu(
		@PathVariable Long storeId,
		@PathVariable Long menuId
	) {

		menuService.deleteMenu(storeId, menuId);

		return new ResponseEntity<>(HttpStatus.OK);
	}
}
