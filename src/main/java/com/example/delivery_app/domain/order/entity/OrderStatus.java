package com.example.delivery_app.domain.order.entity;

public enum OrderStatus {
	REQUESTED, // 주문 요청
	ACCEPTED, // 수락 & 배달중
	DELIVERED, // 배달완료
	CANCELLED // 취소
}