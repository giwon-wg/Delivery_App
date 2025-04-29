package com.example.delivery_app.domain.store.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 가게 삭제(또는 비활성화) 후 응답으로 반환되는 DTO입니다.
 * 삭제된 가게의 ID를 포함합니다.
 */
@Getter
@RequiredArgsConstructor
public class StoreDeleteResponseDto {
	private final Long storeId;
}
