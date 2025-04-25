package com.example.delivery_app.api.region.controller;

import com.example.delivery_app.api.region.dto.RegionResponse;
import com.example.delivery_app.api.region.service.RegionService;
import lombok.RequiredArgsConstructor;

import org.json.JSONException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/region")
@RequiredArgsConstructor
public class RegionController {

	private final RegionService regionService;

	@GetMapping
	public RegionResponse getRegionInfo(
		@RequestParam double lat,
		@RequestParam double lng
	) throws JSONException {
		return regionService.getRegionData(lat, lng);
	}
}