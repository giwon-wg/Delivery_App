package com.example.delivery_app.domain.menu.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.delivery_app.common.dto.CommonResponseDto;
import com.example.delivery_app.domain.menu.dto.requestdto.MenuRequestDto;
import com.example.delivery_app.domain.menu.dto.requestdto.UpdateMenuRequestDto;
import com.example.delivery_app.domain.menu.dto.responsedto.DeleteResponseDto;
import com.example.delivery_app.domain.menu.dto.responsedto.MenuResponseDto;
import com.example.delivery_app.domain.menu.dto.responsedto.MenuSuccessCode;
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
	@PreAuthorize("hasRole('OWNER')")
	@PostMapping
	public ResponseEntity<CommonResponseDto<MenuResponseDto>> saveMenu(
		@PathVariable Long storeId,
		@Valid @RequestBody MenuRequestDto dto
	) {

		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(
				CommonResponseDto.of(
					MenuSuccessCode.MENU_CREATE_SUCCESS,
					menuService.saveMenu(storeId, dto)));
	}

	/**
	 * 메뉴 수정
	 * @param storeId
	 * @param menuId
	 * @param dto
	 * @return
	 */
	@PreAuthorize("hasRole('OWNER')")
	@PatchMapping("/{menuId}")
	public ResponseEntity<CommonResponseDto<UpdateMenuResponseDto>> updateMenu(
		@PathVariable Long storeId,
		@PathVariable Long menuId,
		@Valid @RequestBody UpdateMenuRequestDto dto
	) {

		return ResponseEntity.ok(
			CommonResponseDto.of(
				MenuSuccessCode.MENU_PATCH_SUCCESS,
				menuService.updateMenu(storeId, menuId, dto)
			)
		);
	}

	/**
	 * 메뉴 삭제
	 * Menu Entity의 updateStatus 메서드를 이용하여 soft delete로 구현하였습니다
	 * @param storeId
	 * @param menuId
	 * @return
	 */
	@PreAuthorize("hasRole('OWNER')")
	@DeleteMapping("/{menuId}")
	public ResponseEntity<CommonResponseDto<DeleteResponseDto>> deleteMenu(
		@PathVariable Long storeId,
		@PathVariable Long menuId
	) {

		return ResponseEntity.ok(
			CommonResponseDto.of(
				MenuSuccessCode.MENU_DELETE_SUCCESS,
				menuService.deleteMenu(storeId, menuId)
			)
		);
	}

	/**
	 * JPQL 확인을 위한 임시 메서드
	 * @param storeId
	 * @return
	 */
	@GetMapping
	public ResponseEntity<CommonResponseDto<List<MenuResponseDto>>> findAll(
		@PathVariable Long storeId
	) {
		return ResponseEntity.ok(
			CommonResponseDto.of(
				MenuSuccessCode.MENU_GET_SUCCESS,
				menuService.findAll(storeId)
			)
		);
	}
}
