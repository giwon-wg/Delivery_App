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

	/**
	 * 가게 ID
	 */
	private Long storeId;

	/**
	 * 가게 이름
	 */
	private String storeName;

	/**
	 * 음식 카테고리 (예: 중식, 한식, 양식 등)
	 */
	private String foodCategory;

	/**
	 * 가게 주소
	 */
	private String storeAddress;

	/**
	 * 가게 전화번호
	 */
	private String storePhone;

	/**
	 * 가게 소개 문구
	 */
	private String storeIntro;

	/**
	 * 최소 주문 금액
	 */
	private int minDeliveryPrice;

	/**
	 * 배달팁
	 */
	private int deliveryTip;

	/**
	 * 가게 평점 (예: 4.3)
	 */
	private double rating;

	/**
	 * 리뷰 개수
	 */
	private int reviewCount;

	/**
	 * 가게 영업 상태 (OPEN, CLOSE)
	 */
	private String isOpen;

	/**
	 * 영업 시작 시간 (예: "09:00")
	 */
	private String openTime;

	/**
	 * 영업 종료 시간 (예: "22:00")
	 */
	private String closeTime;

	/**
	 * 가게에서 제공하는 메뉴 리스트
	 */
	private List<MenuResponseDto> menus;

	/**
	 * Store 엔티티로부터 StoreResponseDto 객체를 생성합니다.
	 * soft delete된 메뉴는 제외됩니다.
	 *
	 * @param store Store 엔티티 객체
	 * @return StoreResponseDto 변환 객체
	 */
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
