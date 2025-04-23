package com.example.delivery_app.domain.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.delivery_app.domain.menu.entity.Menu;

public interface MenuRepository extends JpaRepository<Menu, Long> {
}
