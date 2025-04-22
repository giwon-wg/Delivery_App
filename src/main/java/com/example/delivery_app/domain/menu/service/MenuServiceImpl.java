package com.example.delivery_app.domain.menu.service;

import org.springframework.stereotype.Service;

import com.example.delivery_app.domain.menu.dto.requestdto.MenuRequestDto;
import com.example.delivery_app.domain.menu.dto.responsedto.MenuResponseDto;
import com.example.delivery_app.domain.menu.repository.MenuRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

	private MenuRepository menuRepository;
	// private StoreRepository storeRepository;

	@Override
	public MenuResponseDto saveMenu(Long id, MenuRequestDto dto) {

		// Store store = storeRepository.findByIdOrElseThrow(id);
		//
		// Menu menu = new Menu(store, dto);
		//
		// Menu savedMenu = menuRepository.save(menu);
		//
		// return new MenuResponseDto(savedMenu);
		return null;
	}
}
