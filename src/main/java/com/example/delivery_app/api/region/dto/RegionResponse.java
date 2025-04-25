package com.example.delivery_app.api.region.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegionResponse {
	private String region;
	private String logoUrl;
	private String slogan;
	private String recommendedMenu;
}