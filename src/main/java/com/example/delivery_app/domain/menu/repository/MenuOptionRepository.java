package com.example.delivery_app.domain.menu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.delivery_app.domain.menu.entity.MenuOption;

public interface MenuOptionRepository extends JpaRepository<MenuOption, Long> {
	List<MenuOption> findAllByMenu_Id(Long menuId);
}
