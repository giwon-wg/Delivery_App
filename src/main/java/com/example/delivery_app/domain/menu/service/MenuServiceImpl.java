package com.example.delivery_app.domain.menu.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.delivery_app.domain.menu.dto.requestdto.MenuRequestDto;
import com.example.delivery_app.domain.menu.dto.requestdto.UpdateMenuRequestDto;
import com.example.delivery_app.domain.menu.dto.responsedto.DeleteResponseDto;
import com.example.delivery_app.domain.menu.dto.responsedto.MenuResponseDto;
import com.example.delivery_app.domain.menu.dto.responsedto.UpdateMenuResponseDto;
import com.example.delivery_app.domain.menu.entity.Menu;
import com.example.delivery_app.domain.menu.exception.CustomException;
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

	@Transactional
	@Override
	public MenuResponseDto saveMenu(Long storeId, MenuRequestDto dto) {

		Store findStore = storeRepository.findById(storeId)
			.orElseThrow(() -> new IllegalArgumentException("해당 가게가 존재하지 않습니다. id=" + storeId));

		Menu menu = Menu.builder()
			.store(findStore)
			.category(dto.getCategory())
			.menuPicture(dto.getMenuPicture())
			.menuName(dto.getMenuName())
			.price(dto.getPrice())
			.menuContent(dto.getMenuContent())
			.build();

		Menu savedMenu = menuRepository.save(menu);

		return MenuResponseDto.fromMenu(savedMenu);
	}

	@Transactional
	@Override
	public UpdateMenuResponseDto updateMenu(Long storeId, Long menuId, UpdateMenuRequestDto dto) {

		List<Menu> findMenus = menuRepository.findAllByStore_StoreIdAndIsDeleted(storeId, false);

		for (Menu menu : findMenus) {
			if (menuId.equals(menu.getId())) {
				menu.update(dto);
				return new UpdateMenuResponseDto(menu);
			}
		}

		throw new CustomException(ErrorCode.MENU_NOT_FOUND);
	}

	@Transactional
	@Override
	public DeleteResponseDto deleteMenu(Long storeId, Long menuId) {
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
	 * @return
	 */
	@Transactional
	@Override
	public List<MenuResponseDto> findMenu(Long storeId, String word) {

		if (word != null) {
			List<Menu> findMenus = menuRepository.findAllByStore_StoreIdAndMenuNameContainingAndIsDeleted(storeId, word,
				false);
			return findMenus.stream().map(MenuResponseDto::fromMenu).toList();
		}

		List<Menu> findMenus = menuRepository.findAllByStore_StoreId(storeId);
		return findMenus.stream().map(MenuResponseDto::fromMenu).toList();
	}
}
