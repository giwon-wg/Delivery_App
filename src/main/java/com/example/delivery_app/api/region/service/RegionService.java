package com.example.delivery_app.api.region.service;

import com.example.delivery_app.api.region.dto.RegionResponse;
import com.example.delivery_app.api.region.external.KakaoAddressClient;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegionService {

	private final KakaoAddressClient kakaoClient;

	public RegionResponse getRegionData(double lat, double lng) {
		String region = kakaoClient.getRegionName(lat, lng);
		System.out.println(lat + " 이랑 " + lng);
		System.out.println(region);

		if (region.startsWith("서울")) {
			return new RegionResponse("서울특별시", "/logos/seoul.png", "여기요!", "불고기, 설렁탕");
		} else if (region.startsWith("부산")) {
			return new RegionResponse("부산광역시", "/logos/busan.png", "요봤나!", "돼지국밥, 밀면");
		} else if (region.startsWith("대구")) {
			return new RegionResponse("대구광역시", "/logos/daegu.png", "요기라꼬~", "막창, 납작만두");
		} else if (region.startsWith("인천")) {
			return new RegionResponse("인천광역시", "/logos/incheon.png", "여기요!", "짜장면, 쫄면");
		} else if (region.startsWith("광주")) {
			return new RegionResponse("광주광역시", "/logos/gwangju.png", "여그영~", "떡갈비, 한정식");
		} else if (region.startsWith("대전")) {
			return new RegionResponse("대전광역시", "/logos/daejeon.png", "여기유~", "성심당");
		} else if (region.startsWith("울산")) {
			return new RegionResponse("울산광역시", "/logos/ulsan.png", "여기심더~", "언양불고기, 고래고기");
		} else if (region.startsWith("세종")) {
			return new RegionResponse("세종특별자치시", "/logos/sejong.png", "여기유~", "두부김치");

		} else if (region.contains("경상남도") || region.contains("경상북도")) {
			return new RegionResponse("경상도", "/logos/gyeongsang.png", "여기라!", "안동찜닭, 진주냉면");
		} else if (region.contains("전라남도") || region.contains("전라북도")) {
			return new RegionResponse("전라도", "/logos/jeolla.png", "여그잉~!", "꼬막비빔밥, 홍어삼합");
		} else if (region.contains("충청남도") || region.contains("충청북도")) {
			return new RegionResponse("충청도", "/logos/chungcheong.png", "여기유~", "청국장, 올갱이국");
		} else if (region.contains("강원")) {
			return new RegionResponse("강원도", "/logos/gangwon.png", "요기더래요!", "감자옹심이, 막국수");
		} else if (region.contains("제주")) {
			return new RegionResponse("제주도", "/logos/jeju.png", "요긴게마씸~", "흑돼지, 고기국수");
		} else if (region.contains("경기도")) {
			return new RegionResponse("경기도", "/logos/gyeonggi.png", "여기쥬~", "소불고기 정식");
		}

		return new RegionResponse(region, "/logos/default.png", "요기요!", "김치볶음밥");
	}
}