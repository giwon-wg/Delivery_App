package com.example.delivery_app.domain.store.enums;

/**
 * IsOpen 열거형은 가게의 현재 영업 상태를 나타냅니다.
 * - OPEN: 가게가 현재 영업 중인 상태
 * - CLOSED: 가게가 현재 영업하지 않는 상태
 * Store 엔티티에서 사용되어, 가게가 현재 열려 있는지 여부를 표현합니다.
 */
public enum IsOpen {
	OPEN,
	CLOSED
}
