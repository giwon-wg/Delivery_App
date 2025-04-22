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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "orders")
@NoArgsConstructor
@Getter
public class Order extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id")
	private Store store;

	@OneToOne
	@JoinColumn(name = "menu_id")
	private Menu menu;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OrderStatus status = OrderStatus.REQUESTED;

	@Column(nullable = false, columnDefinition = "INT DEFAULT 0")
	private Integer totalPrice = 0;

	// 생성자
	@Builder
	public Order(User user, Store store, Menu menu, OrderStatus status) {
		this.user = user;
		this.store = store;
		this.menu = menu;
		this.totalPrice = menu.getPrice();
		this.status = status;
	}

	/**
	 *🚀 주문정보의 상태를 변경하는 메서드
	 * @param status 주문 상태 enum
	 */
	public void setOrderStatus(OrderStatus status) {
		this.status = status;
	}
}
