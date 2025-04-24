package com.example.delivery_app.domain.store.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 가게 등록, 수정을 위한 RequestDto 입니다.
 */
@Getter
@RequiredArgsConstructor
public class StoreRequestDto {
	/**
	 * 가게의 이름 입니다.
	 */
	@JsonProperty("storeName")
	private final String storeName;

	/**
	 * 가게의 주소 입니다.
	 */
	@JsonProperty("storeAddress")
	private final String storeAddress;

	/**
	 * 가게가 파는 음식의 카테고리(중식,양식,일식 등 입니다.)
	 */
	@JsonProperty("foodCategory")
	private final String foodCategory;

	/**
	 * 가게의 전화번호 입니다.
	 */
	@JsonProperty("storePhone")
	private final String storePhone;

	/**
	 * 가게 소개 문구 입니다. 비워 둘 수 있습니다.
	 */
	private final String storeIntro;

	/**
	 * 가게의 최소 주문 금액입니다.
	 */
	@JsonProperty("minDeliveryPrice")
	private final int minDeliveryPrice;

	/**
	 * 가게의 배달 팁 금액 입니다.
	 */
	@JsonProperty("deliveryTip")
	private final int deliveryTip;

}
