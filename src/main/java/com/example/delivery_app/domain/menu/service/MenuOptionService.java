package com.example.delivery_app.domain.menu.service;

import java.util.List;

import com.example.delivery_app.domain.menu.dto.requestdto.MenuOptionRequestDto;
import com.example.delivery_app.domain.menu.dto.responsedto.MenuOptionResponseDto;

public interface MenuOptionService {

	MenuOptionResponseDto optionSave(Long storeId, Long menuId, MenuOptionRequestDto dto);

	List<MenuOptionResponseDto> findAllOption(Long storeId, Long menuId);
}
