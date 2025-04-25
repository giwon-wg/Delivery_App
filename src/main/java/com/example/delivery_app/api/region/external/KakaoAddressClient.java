package com.example.delivery_app.api.region.external;

import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class KakaoAddressClient {

	@Value("${kakao.api-key}")
	private String apiKey;

	public String getRegionName(double lat, double lng) {
		String url = String.format(
			"https://dapi.kakao.com/v2/local/geo/coord2regioncode.json?x=%f&y=%f",
			lng, lat
		);
		System.out.println("🔍 Kakao API 요청: " + url);

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", apiKey);

		HttpEntity<Void> request = new HttpEntity<>(headers);
		ResponseEntity<String> response = new RestTemplate().exchange(url, HttpMethod.GET, request, String.class);

		JSONObject json = new JSONObject(response.getBody());
		JSONArray documents = json.getJSONArray("documents");

		if (documents.isEmpty()) {
			return "알 수 없음";
		}

		return documents.getJSONObject(0).getString("region_1depth_name");
	}
}
