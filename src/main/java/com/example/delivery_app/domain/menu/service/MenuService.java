package com.example.delivery_app.domain.menu.service;

import com.example.delivery_app.domain.menu.dto.requestdto.MenuRequestDto;
import com.example.delivery_app.domain.menu.dto.requestdto.UpdateMenuRequestDto;
import com.example.delivery_app.domain.menu.dto.responsedto.DeleteResponseDto;
import com.example.delivery_app.domain.menu.dto.responsedto.MenuResponseDto;
import com.example.delivery_app.domain.menu.dto.responsedto.UpdateMenuResponseDto;

public interface MenuService {

	MenuResponseDto saveMenu(Long storeId, MenuRequestDto dto);

	UpdateMenuResponseDto updateMenu(Long storeId, Long menuId, UpdateMenuRequestDto dto);

	DeleteResponseDto deleteMenu(Long storeId, Long menuId);
}
