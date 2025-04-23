package com.example.delivery_app.domain.store.dto.response;

import com.example.delivery_app.domain.store.entity.Store;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StoreResponseDto {

	private Long storeId;

	private String storeName;

	private String foodCategory;

	private String storeAddress;

	private String storePhone;

	private String storeIntro;

	private int minDeliveryPrice;

	private int deliveryTip;

	private double rating;

	private int reviewCount;

	private String isOpen;

	public static StoreResponseDto fromStore(Store store) {
		return StoreResponseDto.builder()
			.storeId(store.getStoreId())
			.storeName(store.getStoreName())
			.foodCategory(store.getFoodCategory())
			.storeAddress(store.getStoreAddress())
			.storePhone(store.getStorePhone())
			.minDeliveryPrice(store.getMinDeliveryPrice())
			.deliveryTip(store.getDeliveryTip())
			.rating(store.getRating())
			.reviewCount(store.getReviewCount())
			.isOpen(store.getIsOpen().toString())
			.build();
	}

}
