package com.example.delivery_app.domain.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoreRequestDto {

	private final Long storeId;

	private final String storeName;

	private final String storeAddress;

	private final String foodCategory;

	private final String storePhone;

	private final String storeIntro;

	private final String minDeliveryPrice;

	private final String deliveryTip;

}
