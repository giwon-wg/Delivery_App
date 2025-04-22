package com.example.delivery_app.domain.menu.entity;

import com.example.delivery_app.common.entity.BaseEntity;
import com.example.delivery_app.domain.store.entity.Store;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Menu extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id")
	private Store store;

	@Column(nullable = false)
	private String category;

	private String menuPicture;

	@Column(nullable = false)
	private String menuName;

	@Column(nullable = false)
	private int price;

	@Column(nullable = false)
	private String menuContent;

	public Menu(Store store, String category, String menuPicture, String menuName, int price, String menuContent) {
		this.store = store;
		this.category = category;
		this.menuPicture = menuPicture;
		this.menuName = menuName;
		this.price = price;
		this.menuContent = menuContent;
	}
}
