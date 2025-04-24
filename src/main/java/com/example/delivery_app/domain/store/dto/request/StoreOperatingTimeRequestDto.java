package com.example.delivery_app.domain.store.dto.request;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StoreOperatingTimeRequestDto {

	@JsonProperty("openTime")
	private final LocalTime openTime;
	@JsonProperty("closeTime")
	private final LocalTime closeTime;
}
