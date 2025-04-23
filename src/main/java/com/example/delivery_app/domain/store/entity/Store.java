package com.example.delivery_app.domain.store.entity;

import com.example.delivery_app.common.entity.BaseEntity;
import com.example.delivery_app.domain.store.enums.IsOpen;
import com.example.delivery_app.domain.store.enums.StoreStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "store")
public class Store extends BaseEntity {

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
	@Enumerated(EnumType.STRING)
	private IsOpen isOpen;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private StoreStatus status;

	@Builder
	public Store(String storeName, String storeAddress, String storeIntro, String storePhone, String foodCategory,
		String minDeliveryPrice, String deliveryTip) {
		this.storeName = storeName;
		this.storeAddress = storeAddress;
		this.storeIntro = storeIntro;
		this.storePhone = storePhone;
		this.foodCategory = foodCategory;
		this.minDeliveryPrice = minDeliveryPrice;
		this.deliveryTip = deliveryTip;
		this.rating = "0.0";
		this.reviewCount = "0";
		this.isOpen = IsOpen.OPEN;
		this.status = StoreStatus.ACTIVE;
	}
}

