package com.example.delivery_app.domain.order.dto.response;

import com.example.delivery_app.domain.menu.entity.Menu;
import com.example.delivery_app.domain.order.entity.OrderStatus;
import com.example.delivery_app.domain.store.entity.Store;
import com.example.delivery_app.domain.user.entity.User;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OrderResponseDto {
	private final User user;
	private final Store store;
	private final Menu menu;
	private final OrderStatus status;
	private final int totalPrice;

	@Builder
	public OrderResponseDto(User user, Store store, Menu menu, OrderStatus status) {
		this.user = user;
		this.store = store;
		this.menu = menu;
		this.status = status;
		this.totalPrice = menu.getPrice();
	}
}