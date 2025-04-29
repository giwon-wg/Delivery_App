package com.example.delivery_app.domain.menu.service;

import java.util.List;

import com.example.delivery_app.domain.menu.dto.requestdto.MenuOptionRequestDto;
import com.example.delivery_app.domain.menu.dto.requestdto.MenuOptionUpdateRequestDto;
import com.example.delivery_app.domain.menu.dto.responsedto.MenuOptionDeleteResponseDto;
import com.example.delivery_app.domain.menu.dto.responsedto.MenuOptionResponseDto;
import com.example.delivery_app.domain.menu.dto.responsedto.MenuOptionUpdateResponseDto;

public interface MenuOptionService {

	MenuOptionResponseDto optionSave(Long storeId, Long menuId, MenuOptionRequestDto dto);

	List<MenuOptionResponseDto> findAllOption(Long storeId, Long menuId);

	MenuOptionUpdateResponseDto updateMenuOption(Long storeId, Long menuId, Long optionId,
		MenuOptionUpdateRequestDto dto);

	MenuOptionDeleteResponseDto deleteMenuOption(Long storeId, Long menuId, Long optionId);
}
