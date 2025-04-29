package com.example.delivery_app.domain.store.enums;

/**
 * StoreStatus 열거형은 가게의 활성화 상태를 나타냅니다.
 * - ACTIVE: 사용자가 정상적으로 운영 중인 상태의 가게
 * - INACTIVE: 폐업 또는 운영 중지 상태로, 사용자에게 노출되지 않음
 * 이 값은 soft delete 또는 비활성화 처리를 위한 상태 관리에 사용됩니다.
 */
public enum StoreStatus {
	ACTIVE,
	INACTIVE
}
