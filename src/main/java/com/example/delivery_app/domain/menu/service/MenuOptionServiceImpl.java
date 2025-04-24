package com.example.delivery_app.domain.menu.service;

import org.springframework.stereotype.Service;

import com.example.delivery_app.domain.menu.dto.requestdto.MenuOptionRequestDto;
import com.example.delivery_app.domain.menu.dto.responsedto.MenuOptionResponseDto;
import com.example.delivery_app.domain.menu.entity.Menu;
import com.example.delivery_app.domain.menu.entity.MenuOption;
import com.example.delivery_app.domain.menu.exception.CustomException;
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

		MenuOption.MenuOptionBuilder builder = MenuOption.builder();
		builder.optionName(dto.getOptionName());
		builder.price(dto.getPrice());
		builder.content(dto.getContent());
		builder.menu(findMenu);
		MenuOption menuOption = builder
			.build();

		menuOptionRepository.save(menuOption);

		return MenuOptionResponseDto.fromMenu(menuOption);
	}
}
