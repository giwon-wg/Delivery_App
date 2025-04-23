package com.example.delivery_app.domain.order.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class OrderRequestDto {
	private final Long storeId;
	private final Long menuId;
}