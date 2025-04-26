package com.example.delivery_app.domain.store.entity;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.example.delivery_app.common.entity.BaseEntity;
import com.example.delivery_app.domain.menu.entity.Menu;
import com.example.delivery_app.domain.store.dto.request.StoreRequestDto;
import com.example.delivery_app.domain.store.enums.IsOpen;
import com.example.delivery_app.domain.store.enums.StoreStatus;
import com.example.delivery_app.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Store 엔티티는 가게 정보를 나타내며,
 * 각 가게는 하나의 사장님(User)에게 속하고 여러 개의 메뉴(Menu)를 가질 수 있습니다.
 */
@Getter
@Entity
@NoArgsConstructor
@Table(name = "store")
public class Store extends BaseEntity {

	/**
	 * 가게의 고유 ID (PK)
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long storeId;

	/**
	 * 가게 이름
	 */
	@Column(nullable = false)
	private String storeName;

	/**
	 * 음식 카테고리 (예: 중식, 한식 등)
	 */
	@Column(nullable = false)
	private String foodCategory;

	/**
	 * 가게 주소
	 */
	@Column(nullable = false)
	private String storeAddress;

	/**
	 * 가게 전화번호
	 */
	@Column(nullable = false)
	private String storePhone;

	/**
	 * 가게 소개 문구 (선택)
	 */
	private String storeIntro;

	/**
	 * 최소 주문 금액
	 */
	@Column(nullable = false)
	private int minDeliveryPrice;

	/**
	 * 배달 팁
	 */
	@Column(nullable = false)
	private int deliveryTip;

	/**
	 * 가게 평점 (기본 0.0)
	 */
	@Column(nullable = false)
	private double rating;

	/**
	 * 리뷰 개수 (기본 0)
	 */
	@Column(nullable = false)
	private int reviewCount;

	/**
	 * 현재 가게의 영업 상태 (OPEN / CLOSED)
	 */
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private IsOpen isOpen;

	/**
	 * 가게의 운영 상태 (ACTIVE / INACTIVE)
	 */
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private StoreStatus status;

	/**
	 * 영업 시작 시간
	 */
	@Column(nullable = false)
	private LocalTime openTime;

	/**
	 * 영업 종료 시간
	 */
	@Column(nullable = false)
	private LocalTime closeTime;

	/**
	 * 가게가 보유한 메뉴 리스트 (연관 관계: 1:N)
	 */
	@OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
	private List<Menu> menus = new ArrayList<>();

	/**
	 * 가게의 소유주 (연관 관계: N:1)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	/**
	 * 가게 객체를 생성하는 빌더 생성자입니다.
	 * 평점과 리뷰 수는 기본값으로 초기화되며,
	 * 현재 시간에 따라 isOpen 값을 자동 계산합니다.
	 */
	@Builder
	public Store(String storeName, String storeAddress, String storeIntro, String storePhone, String foodCategory,
		int minDeliveryPrice, int deliveryTip, LocalTime openTime, LocalTime closeTime, User user) {
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
		this.user = user;
		this.status = StoreStatus.ACTIVE;
		LocalTime now = LocalTime.now();
		this.isOpen = (now.isAfter(openTime) && now.isBefore(closeTime)) ? IsOpen.OPEN : IsOpen.CLOSED;
	}

	/**
	 * 가게 정보를 수정합니다.
	 *
	 * @param storeRequestDto 수정할 요청 DTO
	 */
	public void updateStoreInfo(StoreRequestDto storeRequestDto) {
		this.storeName = storeRequestDto.getStoreName();
		this.foodCategory = storeRequestDto.getFoodCategory();
		this.storeAddress = storeRequestDto.getStoreAddress();
		this.storePhone = storeRequestDto.getStorePhone();
		this.storeIntro = storeRequestDto.getStoreIntro();
		this.minDeliveryPrice = storeRequestDto.getMinDeliveryPrice();
		this.deliveryTip = storeRequestDto.getDeliveryTip();
	}

	/**
	 * 영업 시작/종료 시간을 업데이트합니다.
	 *
	 * @param openTime  새로운 오픈 시간
	 * @param closeTime 새로운 마감 시간
	 */
	public void updateOperatingTime(LocalTime openTime, LocalTime closeTime) {
		this.openTime = openTime;
		this.closeTime = closeTime;
	}

	/**
	 * 현재 영업 상태(isOpen)를 직접 수정합니다.
	 * (스케줄러나 API를 통해 상태 전환 시 사용 가능)
	 *
	 * @param isOpen 새로운 영업 상태
	 */
	public void updateOpenStatus(IsOpen isOpen) {
		this.isOpen = isOpen;
	}

	/**
	 * 가게 상태를 INACTIVE(폐업)으로 변경합니다.
	 */
	public void markAsInactive() {
		this.status = StoreStatus.INACTIVE;
	}
}

