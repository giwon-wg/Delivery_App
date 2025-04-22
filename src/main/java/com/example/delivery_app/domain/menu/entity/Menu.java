package com.example.delivery_app.domain.menu.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Menu {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String category;

	private String menuPicture;

	@Column(nullable = false)
	private String menuName;

	@Column(nullable = false)
	private int price;

	@Column(nullable = false)
	private String menuContent;

	public Menu() {
	}

	public Menu(String category, String menuPicture, String menuName, int price, String menuContent) {
		this.category = category;
		this.menuPicture = menuPicture;
		this.menuName = menuName;
		this.price = price;
		this.menuContent = menuContent;
	}
}
