package com.example.delivery_app.domain.store.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StoreRequestDto {

	private final String storeName;

	private final String storeAddress;

	private final String foodCategory;

	private final String storePhone;

	private final String storeIntro;

	private final String minDeliveryPrice;

	private final String deliveryTip;

}
