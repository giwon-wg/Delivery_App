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
import com.example.delivery_app.domain.menu.repository.MenuRepository;
import com.example.delivery_app.domain.store.entity.Store;
import com.example.delivery_app.domain.store.repository.StoreRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuOptionServiceImpl implements MenuOptionService {

	private final StoreRepository storeRepository;
	private final MenuRepository menuRepository;
	private final MenuOptionRepository menuOptionRepository;

	/**
	 * 메뉴 옵션 저장 기능
	 * @param storeId
	 * @param menuId
	 * @param dto
	 * @return
	 */
	@Transactional
	@Override
	public MenuOptionResponseDto optionSave(Long storeId, Long menuId, MenuOptionRequestDto dto) {

		Store findStore = storeRepository.findById(storeId)
			.orElseThrow(() -> new IllegalArgumentException("해당 가게가 존재하지 않습니다. id=" + storeId));

		Menu findMenu = menuRepository.findByIdOrElseThrow(menuId);

		if (!findMenu.getStore().getStoreId().equals(findStore.getStoreId())) {
			throw new CustomException(ErrorCode.MISMATCH_ERROR);
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
	 * @return
	 */
	@Transactional
	@Override
	public List<MenuOptionResponseDto> findAllOption(Long storeId, Long menuId) {

		Store findStore = storeRepository.findById(storeId)
			.orElseThrow(() -> new IllegalArgumentException("해당 가게가 존재하지 않습니다. id=" + storeId));

		Menu findMenu = menuRepository.findByIdOrElseThrow(menuId);

		if (!findMenu.getStore().getStoreId().equals(findStore.getStoreId())) {
			throw new CustomException(ErrorCode.MISMATCH_ERROR);
		}

		List<MenuOption> findAllOption = menuOptionRepository.findAllByMenu_IdAndIsDeleted(menuId, false);

		return findAllOption.stream().map(MenuOptionResponseDto::fromMenuOption).toList();
	}

	/**
	 * 메뉴 옵션 수정 기능
	 * @param storeId
	 * @param menuId
	 * @param optionId
	 * @param dto
	 * @return
	 */
	@Transactional
	@Override
	public MenuOptionUpdateResponseDto updateMenuOption(Long storeId, Long menuId, Long optionId,
		MenuOptionUpdateRequestDto dto) {

		Store findStore = storeRepository.findById(storeId)
			.orElseThrow(() -> new IllegalArgumentException("해당 가게가 존재하지 않습니다. id=" + storeId));

		Menu findMenu = menuRepository.findByIdOrElseThrow(menuId);

		if (!findMenu.getStore().getStoreId().equals(findStore.getStoreId())) {
			throw new CustomException(ErrorCode.MISMATCH_ERROR);
		}

		MenuOption findMenuOption = menuOptionRepository.findByIdOrElseThrow(optionId);

		if (findMenuOption.isDeleted()) {
			throw new CustomException(ErrorCode.MENU_ALREADY_DELETED);
		}

		findMenuOption.updateMenuOption(dto);

		return MenuOptionUpdateResponseDto.fromMenuOption(findMenuOption);
	}

	/**
	 * 메뉴 옵션 삭제 기능
	 * @param storeId
	 * @param menuId
	 * @param optionId
	 * @return
	 */
	@Transactional
	@Override
	public MenuOptionDeleteResponseDto deleteMenuOption(Long storeId, Long menuId, Long optionId) {

		Store findStore = storeRepository.findById(storeId)
			.orElseThrow(() -> new IllegalArgumentException("해당 가게가 존재하지 않습니다. id=" + storeId));

		Menu findMenu = menuRepository.findByIdOrElseThrow(menuId);

		if (!findMenu.getStore().getStoreId().equals(findStore.getStoreId())) {
			throw new CustomException(ErrorCode.MISMATCH_ERROR);
		}

		MenuOption findMenuOption = menuOptionRepository.findByIdOrElseThrow(optionId);

		findMenuOption.deleteMenuOption();

		return MenuOptionDeleteResponseDto.fromMenuOption(findMenuOption);
	}

}
