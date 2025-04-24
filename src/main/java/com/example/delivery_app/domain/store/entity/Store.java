package com.example.delivery_app.domain.store.entity;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.example.delivery_app.common.entity.BaseEntity;
import com.example.delivery_app.domain.menu.entity.Menu;
import com.example.delivery_app.domain.store.dto.request.StoreRequestDto;
import com.example.delivery_app.domain.store.enums.IsOpen;
import com.example.delivery_app.domain.store.enums.StoreStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
	private int minDeliveryPrice;

	@Column(nullable = false)
	private int deliveryTip;

	@Column(nullable = false)
	private double rating;

	@Column(nullable = false)
	private int reviewCount;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private IsOpen isOpen;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private StoreStatus status;

	@Column(nullable = false)
	private LocalTime openTime;

	@Column(nullable = false)
	private LocalTime closeTime;

	@OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
	private List<Menu> menus = new ArrayList<>();

	@Builder
	public Store(String storeName, String storeAddress, String storeIntro, String storePhone, String foodCategory,
		int minDeliveryPrice, int deliveryTip, LocalTime openTime, LocalTime closeTime) {
		this.storeName = storeName;
		this.storeAddress = storeAddress;
		this.storeIntro = storeIntro;
		this.storePhone = storePhone;
		this.foodCategory = foodCategory;
		this.minDeliveryPrice = minDeliveryPrice;
		this.deliveryTip = deliveryTip;
		this.openTime = openTime;
		this.closeTime = closeTime;
		this.rating = 0.0;
		this.reviewCount = 0;
		this.isOpen = IsOpen.OPEN;
		this.status = StoreStatus.ACTIVE;
	}

	public void updateStoreInfo(StoreRequestDto storeRequestDto) {
		this.storeName = storeRequestDto.getStoreName();
		this.foodCategory = storeRequestDto.getFoodCategory();
		this.storeAddress = storeRequestDto.getStoreAddress();
		this.storePhone = storeRequestDto.getStorePhone();
		this.storeIntro = storeRequestDto.getStoreIntro();
		this.minDeliveryPrice = storeRequestDto.getMinDeliveryPrice();
		this.deliveryTip = storeRequestDto.getDeliveryTip();
	}

	public void updateOperatingTime(LocalTime openTime, LocalTime closeTime) {
		this.openTime = openTime;
		this.closeTime = closeTime;
	}

	public void updateOpenStatus(IsOpen isOpen) {
		this.isOpen = isOpen;
	}

	public void markAsInactive() {
		this.status = StoreStatus.INACTIVE;
	}
}

