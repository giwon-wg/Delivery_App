package com.example.delivery_app.domain.menu.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "menuoption")
public class MenuOption {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String optionName;

	private Integer price;

	private String content;

	private boolean isDeleted;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "menu_id", nullable = false)
	private Menu menu;

	@Builder
	public MenuOption(String optionName, Integer price, String content, boolean isDeleted, Menu menu) {
		this.optionName = optionName;
		this.price = price;
		this.content = content;
		this.isDeleted = isDeleted;
		this.menu = menu;
	}

}
