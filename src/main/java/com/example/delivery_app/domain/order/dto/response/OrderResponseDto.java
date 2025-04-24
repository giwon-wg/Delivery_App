package com.example.delivery_app.domain.order.dto.response;

import com.example.delivery_app.domain.order.entity.OrderStatus;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Getter;

@Getter
@JsonPropertyOrder({"orderId", "userId", "storeId", "menuId", "status", "totalPrice"})
@Builder
public class OrderResponseDto {
	private final Long orderId;
	private final Long userId;
	private final Long storeId;
	private final Long menuId;
	private final OrderStatus status;
	private final Integer totalPrice;
}