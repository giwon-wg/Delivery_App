package com.example.delivery_app.domain.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.delivery_app.domain.menu.entity.MenuOption;

public interface MenuOptionRepository extends JpaRepository<MenuOption, Long> {
}
