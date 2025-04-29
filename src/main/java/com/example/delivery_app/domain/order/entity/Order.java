package com.example.delivery_app.domain.order.entity;

import com.example.delivery_app.common.entity.BaseEntity;
import com.example.delivery_app.domain.menu.entity.Menu;
import com.example.delivery_app.domain.store.entity.Store;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "orders")
@Table(name = "orders")
@NoArgsConstructor
@Getter
public class Order extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;

	@OneToOne
	@JoinColumn(name = "menu_id", nullable = false)
	private Menu menu;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OrderStatus status;

	@Column(nullable = false, columnDefinition = "INT DEFAULT 0")
	private int totalPrice = 0;

	// 생성자
	@Builder
	public Order(User user, Store store, Menu menu) {
		this.user = user;
		this.store = store;
		this.menu = menu;
		this.totalPrice = calculateTotalPrice(store.getDeliveryTip(), menu.getPrice());
		this.status = OrderStatus.REQUESTED;
	}

	/**
	 * 🚀 주문정보의 상태를 변경하는 메서드
	 * @param status 주문 상태 enum
	 */
	public void setOrderStatus(OrderStatus status) {
		this.status = status;
	}

	/**
	 * 🚀총 주문금액을 계산하는 메서드
	 * @param deliveryTip 배달 팁
	 * @param menuPrice 메뉴 가격
	 * @return 총 주문 금액을 반환
	 */
	private int calculateTotalPrice(int deliveryTip, int menuPrice) {
		return deliveryTip + menuPrice;
	}
}
