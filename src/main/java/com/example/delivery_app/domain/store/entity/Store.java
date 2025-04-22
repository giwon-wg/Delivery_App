package com.example.delivery_app.domain.store.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "store")
public class Store {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long storeId;

	@Column(nullable = false)
	private String storeName;

	@Column(nullable = false)
	private String foodCategory;

	@Column(nullable = false)
	private String storeAddress;

	@Column(nullable = false)
	private String storePhone;

	private String storeIntro;

	@Column(nullable = false)
	private String minDeliveryPrice;

	@Column(nullable = false)
	private String deliveryTip;

	@Column(nullable = false)
	private String rating;

	@Column(nullable = false)
	private String reviewCount;

	@Column(nullable = false)
	private String isOpen;
}
