package com.example.delivery_app.domain.menu.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.delivery_app.common.exception.CustomException;
import com.example.delivery_app.domain.menu.dto.requestdto.MenuRequestDto;
import com.example.delivery_app.domain.menu.dto.requestdto.UpdateMenuRequestDto;
import com.example.delivery_app.domain.menu.dto.responsedto.DeleteResponseDto;
import com.example.delivery_app.domain.menu.dto.responsedto.MenuCreateResponseDto;
import com.example.delivery_app.domain.menu.dto.responsedto.MenuResponseDto;
import com.example.delivery_app.domain.menu.dto.responsedto.UpdateMenuResponseDto;
import com.example.delivery_app.domain.menu.entity.Menu;
import com.example.delivery_app.domain.menu.exception.ErrorCode;
import com.example.delivery_app.domain.menu.repository.MenuRepository;
import com.example.delivery_app.domain.store.entity.Store;
import com.example.delivery_app.domain.store.repository.StoreRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

	private final MenuRepository menuRepository;
	private final StoreRepository storeRepository;

	/**
	 * 메뉴 저장 기능
	 * @param storeId
	 * @param dto
	 * @return MenuCreateResponseDto 반환
	 */
	@Transactional
	@Override
	public MenuCreateResponseDto saveMenu(Long storeId, MenuRequestDto dto) {

		Store findStore = storeRepository.findByIdOrElseThrow(storeId);

		Menu menu = Menu.builder()
			.store(findStore)
			.category(dto.getCategory())
			.menuPicture(dto.getMenuPicture())
			.menuName(dto.getMenuName())
			.price(dto.getPrice())
			.menuContent(dto.getMenuContent())
			.build();

		Menu savedMenu = menuRepository.save(menu);

		return MenuCreateResponseDto.menuFrom(savedMenu);
	}

	/**
	 * 메뉴 수정 기능
	 * @param storeId
	 * @param menuId
	 * @param dto
	 * @return UpdateMenuResponseDto 반환
	 */
	@Transactional
	@Override
	public UpdateMenuResponseDto updateMenu(Long storeId, Long menuId, UpdateMenuRequestDto dto) {

		storeRepository.findByIdOrElseThrow(storeId);

		List<Menu> findMenus = menuRepository.findAllByStore_StoreIdAndIsDeleted(storeId, false);

		for (Menu menu : findMenus) {
			if (menuId.equals(menu.getId())) {
				if (!menu.getMenuName().equals(dto.getMenuName())) {
					menu.update(dto);
					return new UpdateMenuResponseDto(menu);
				} else {
					throw new CustomException(ErrorCode.INVALID_MENU_NAME);
				}
			}
		}
		throw new CustomException(ErrorCode.MENU_NOT_FOUND);
	}

	/**
	 * 메뉴 삭제 기능
	 * @param storeId
	 * @param menuId
	 * @return DeleteResponseDto 반환
	 */
	@Transactional
	@Override
	public DeleteResponseDto deleteMenu(Long storeId, Long menuId) {

		storeRepository.findByIdOrElseThrow(storeId);

		List<Menu> findMenus = menuRepository.findAllByStore_StoreIdAndIsDeleted(storeId, false);

		for (Menu menu : findMenus) {
			if (menuId.equals(menu.getId())) {
				menu.deleteMenu();
				return new DeleteResponseDto(menu);
			}
		}

		throw new CustomException(ErrorCode.MENU_NOT_FOUND);
	}

	/**
	 * 검색 기능 구현을 위해 추가
	 * 일부 단어만 입력하여도 그와 관련된 메뉴들이 출력
	 * @param storeId
	 * @param word
	 * @return List<MenuResponseDto> 반환
	 */
	@Transactional
	@Override
	public List<MenuResponseDto> findMenu(Long storeId, String word) {

		storeRepository.findByIdOrElseThrow(storeId);

		if (word != null) {
			List<Menu> findMenus = menuRepository.findAllByStore_StoreIdAndMenuNameContainingAndIsDeleted(storeId,
				word,
				false);
			return findMenus.stream().map(MenuResponseDto::fromMenu).toList();
		}

		List<Menu> findMenus = menuRepository.findAllByStore_StoreIdAndIsDeleted(storeId, false);
		return findMenus.stream().map(MenuResponseDto::fromMenu).toList();
	}
}
