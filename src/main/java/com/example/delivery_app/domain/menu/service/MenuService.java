package com.example.delivery_app.domain.menu.service;

import com.example.delivery_app.domain.menu.dto.requestdto.MenuRequestDto;
import com.example.delivery_app.domain.menu.dto.responsedto.MenuResponseDto;

public interface MenuService {

	MenuResponseDto saveMenu(Long id, MenuRequestDto dto);
}
