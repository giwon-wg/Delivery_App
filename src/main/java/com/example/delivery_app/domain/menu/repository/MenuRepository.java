package com.example.delivery_app.domain.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.delivery_app.domain.menu.entity.Menu;
import com.example.delivery_app.domain.menu.exception.CustomException;
import com.example.delivery_app.domain.menu.exception.ErrorCode;

public interface MenuRepository extends JpaRepository<Menu, Long> {

	default Menu findByIdOrElseThrow(Long menuId) {
		return findById(menuId).orElseThrow(() -> new CustomException(ErrorCode.MENU_NOT_FOUND));
	}
}
