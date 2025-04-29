package com.example.delivery_app.domain.store.dto.request;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 가게의 영업 시작 시간과 종료 시간을 전달받기 위한 요청 DTO입니다.
 */
@Getter
@RequiredArgsConstructor
public class StoreOperatingTimeRequestDto {

	/**
	 * 가게의 영업 시작 시간입니다.
	 * 예시: "09:00"
	 */
	@JsonProperty("openTime")
	private final LocalTime openTime;

	/**
	 * 가게의 영업 종료 시간입니다.
	 * 예시: "22:00"
	 */
	@JsonProperty("closeTime")
	private final LocalTime closeTime;
}
