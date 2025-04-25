package com.example.delivery_app.domain.store.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import com.example.delivery_app.domain.menu.dto.responsedto.MenuResponseDto;
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

	private String openTime;

	private String closeTime;

	private List<MenuResponseDto> menus;

	public static StoreResponseDto fromStore(Store store) {
		return StoreResponseDto.builder()
			.storeId(store.getStoreId())
			.storeName(store.getStoreName())
			.foodCategory(store.getFoodCategory())
			.storeAddress(store.getStoreAddress())
			.storePhone(store.getStorePhone())
			.storeIntro(store.getStoreIntro())
			.minDeliveryPrice(store.getMinDeliveryPrice())
			.deliveryTip(store.getDeliveryTip())
			.rating(store.getRating())
			.reviewCount(store.getReviewCount())
			.isOpen(store.getIsOpen().toString())
			.openTime(store.getOpenTime().toString())
			.closeTime(store.getCloseTime().toString())
			.menus(store.getMenus().stream()
				.filter(menu -> !menu.isDeleted())
				.map(MenuResponseDto::fromMenu)
				.collect(Collectors.toList()))
			.build();
	}

}
