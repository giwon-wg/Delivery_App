package com.example.delivery_app.domain.order.entity;

import java.awt.*;

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

@Entity
@NoArgsConstructor
@Getter
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	@Column(nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id")
	@Column(nullable = false)
	private Store store;

	@OneToOne
	@JoinColumn(name = "menu_id")
	@Column(nullable = false)
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

	public void setOrderStatus(OrderStatus status) {
		this.status = status;
	}
}
