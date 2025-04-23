package com.example.delivery_app.domain.menu.service;

import org.springframework.stereotype.Service;

import com.example.delivery_app.domain.menu.dto.requestdto.MenuRequestDto;
import com.example.delivery_app.domain.menu.dto.requestdto.UpdateMenuRequestDto;
import com.example.delivery_app.domain.menu.dto.responsedto.MenuResponseDto;
import com.example.delivery_app.domain.menu.dto.responsedto.UpdateMenuResponseDto;
import com.example.delivery_app.domain.menu.entity.Menu;
import com.example.delivery_app.domain.menu.repository.MenuRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

	private final MenuRepository menuRepository;
	// private StoreRepository storeRepository;

	@Override
	public MenuResponseDto saveMenu(Long storeId, MenuRequestDto dto) {

		// Store findStore = storeRepository.findByIdOrElseThrow(storeId);
		//
		// Menu menu = new Menu(findStore, dto);
		//
		// Menu savedMenu = menuRepository.save(menu);
		//
		// return new MenuResponseDto(savedMenu);
		return null;
	}

	@Transactional
	@Override
	public UpdateMenuResponseDto updateMenu(Long storeId, Long menuId, UpdateMenuRequestDto dto) {

		Menu findMenu = menuRepository.findByIdOrElseThrow(menuId);

		findMenu.update(dto);

		return new UpdateMenuResponseDto(findMenu);
	}
}
