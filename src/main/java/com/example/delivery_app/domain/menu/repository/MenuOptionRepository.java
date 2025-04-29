package com.example.delivery_app.domain.menu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.delivery_app.common.exception.CustomException;
import com.example.delivery_app.domain.menu.entity.MenuOption;
import com.example.delivery_app.domain.menu.exception.ErrorCode;

public interface MenuOptionRepository extends JpaRepository<MenuOption, Long> {
	List<MenuOption> findAllByMenu_Id(Long menuId);

	default MenuOption findByIdOrElseThrow(Long optionId) {
		return findById(optionId).orElseThrow(() -> new CustomException(ErrorCode.MENU_OPTION_NOT_FOUND));
	}

	List<MenuOption> findAllByMenu_IdAndIsDeleted(Long menuId, boolean status);

	;
}
