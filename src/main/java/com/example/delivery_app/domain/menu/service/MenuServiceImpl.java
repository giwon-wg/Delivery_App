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

		Menu menu = new Menu(findStore, dto);

		Menu savedMenu = menuRepository.save(menu);

		return new MenuResponseDto(savedMenu);
	}

	@Transactional
	@Override
	public UpdateMenuResponseDto updateMenu(Long storeId, Long menuId, UpdateMenuRequestDto dto) {

		List<Menu> findMenus = menuRepository.findByIdOrElseThrow(storeId, true);

		for (Menu menu : findMenus) {
			if (menuId.equals(menu.getId())) {
				menu.update(dto);
				menuRepository.save(menu);
				return new UpdateMenuResponseDto(menu);
			}
		}

		throw new CustomException(ErrorCode.MENU_NOT_FOUND);
	}

	@Transactional
	@Override
	public DeleteResponseDto deleteMenu(Long storeId, Long menuId) {
		List<Menu> findMenus = menuRepository.findByIdOrElseThrow(storeId, true);

		for (Menu menu : findMenus) {
			if (menuId.equals(menu.getId())) {
				menu.deleteMenu();
				menuRepository.save(menu);
				return new DeleteResponseDto(menu);
			}
		}

		throw new CustomException(ErrorCode.MENU_NOT_FOUND);
	}

	/**
	 * JPQL 확인을 위한 임시 메서드
	 * @param storeId
	 * @return
	 */
	@Override
	public List<MenuResponseDto> findAll(Long storeId) {

		return menuRepository.findAll(true).stream().map(MenuResponseDto::toDto).toList();
	}

	/**
	 * 검색 기능 구현을 위해 추가
	 * @param storeId
	 * @param menuName
	 * @return
	 */
	@Override
	public List<MenuResponseDto> search(Long storeId, String menuName) {

		menuRepository.findByIdOrElseThrow(storeId, true);

		return List.of();
	}
}
