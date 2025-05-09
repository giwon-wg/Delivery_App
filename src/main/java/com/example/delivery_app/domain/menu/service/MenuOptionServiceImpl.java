package com.example.delivery_app.domain.menu.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.delivery_app.common.exception.CustomException;
import com.example.delivery_app.domain.menu.dto.requestdto.MenuOptionRequestDto;
import com.example.delivery_app.domain.menu.dto.requestdto.MenuOptionUpdateRequestDto;
import com.example.delivery_app.domain.menu.dto.responsedto.MenuOptionDeleteResponseDto;
import com.example.delivery_app.domain.menu.dto.responsedto.MenuOptionResponseDto;
import com.example.delivery_app.domain.menu.dto.responsedto.MenuOptionUpdateResponseDto;
import com.example.delivery_app.domain.menu.entity.Menu;
import com.example.delivery_app.domain.menu.entity.MenuOption;
import com.example.delivery_app.domain.menu.exception.ErrorCode;
import com.example.delivery_app.domain.menu.repository.MenuOptionRepository;
import com.example.delivery_app.domain.store.entity.Store;
import com.example.delivery_app.domain.store.repository.StoreRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuOptionServiceImpl implements MenuOptionService {

	private final StoreRepository storeRepository;
	private final MenuOptionRepository menuOptionRepository;

	/**
	 * 메뉴 옵션 저장 기능
	 * @param storeId
	 * @param menuId
	 * @param dto
	 * @return MenuOptionResponseDto 반환
	 */
	@Transactional
	@Override
	public MenuOptionResponseDto optionSave(Long storeId, Long menuId, MenuOptionRequestDto dto) {

		Store findStore = storeRepository.findByIdOrElseThrow(storeId);

		Menu findMenu = findStore.getMenus()
			.stream()
			.filter(menu -> menu.getId().equals(menuId))
			.findFirst()
			.orElseThrow(() -> new CustomException(ErrorCode.MISMATCH_ERROR));

		if (findMenu.isDeleted()) {
			throw new CustomException(ErrorCode.MENU_ALREADY_DELETED);
		}

		MenuOption menuOption = MenuOption.builder()
			.optionName((dto.getOptionName()))
			.price((dto.getPrice()))
			.content((dto.getContent()))
			.menu(findMenu)
			.build();

		menuOptionRepository.save(menuOption);

		return MenuOptionResponseDto.fromMenuOption(menuOption);
	}

	/**
	 * 메뉴 옵션 조회 기능
	 * @param storeId
	 * @param menuId
	 * @return List<MenuOptionResponseDto> 반환
	 */
	@Transactional
	@Override
	public List<MenuOptionResponseDto> findAllOption(Long storeId, Long menuId) {

		checkMismatchError(storeId, menuId);

		List<MenuOption> findAllOption = menuOptionRepository.findAllByMenu_IdAndIsDeleted(menuId, false);

		return findAllOption.stream().map(MenuOptionResponseDto::fromMenuOption).toList();
	}

	/**
	 * 메뉴 옵션 수정 기능
	 * @param storeId
	 * @param menuId
	 * @param optionId
	 * @param dto
	 * @return MenuOptionUpdateResponseDto 반환
	 */
	@Transactional
	@Override
	public MenuOptionUpdateResponseDto updateMenuOption(Long storeId, Long menuId, Long optionId,
		MenuOptionUpdateRequestDto dto) {

		checkMismatchError(storeId, menuId);

		MenuOption findMenuOption = menuOptionRepository.findByIdOrElseThrow(optionId);

		if (findMenuOption.isDeleted()) {
			throw new CustomException(ErrorCode.MENU_OPTION_ALREADY_DELETED);
		}

		if (findMenuOption.getOptionName().equals(dto.getOptionName())) {
			throw new CustomException(ErrorCode.INVALID_MENU_OPTION_NAME);
		}

		findMenuOption.updateMenuOption(dto);

		return MenuOptionUpdateResponseDto.fromMenuOption(findMenuOption);
	}

	/**
	 * 메뉴 옵션 삭제 기능
	 * @param storeId
	 * @param menuId
	 * @param optionId
	 * @return MenuOptionDeleteResponseDto 반환
	 */
	@Transactional
	@Override
	public MenuOptionDeleteResponseDto deleteMenuOption(Long storeId, Long menuId, Long optionId) {

		checkMismatchError(storeId, menuId);

		MenuOption findMenuOption = menuOptionRepository.findByIdOrElseThrow(optionId);

		if (findMenuOption.isDeleted()) {
			throw new CustomException(ErrorCode.MENU_OPTION_ALREADY_DELETED);
		}

		findMenuOption.deleteMenuOption();

		return MenuOptionDeleteResponseDto.fromMenuOption(findMenuOption);
	}

	/**
	 * 검증 메서드
	 * @param storeId
	 * @param menuId
	 */
	private void checkMismatchError(Long storeId, Long menuId) {
		Store findStore = storeRepository.findByIdOrElseThrow(storeId);

		findStore.getMenus()
			.stream()
			.filter(abc -> abc.getId().equals(menuId))
			.findFirst()
			.orElseThrow(() -> new CustomException(ErrorCode.MISMATCH_ERROR));

	}
}
