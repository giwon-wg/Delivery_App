package com.example.delivery_app.domain.store.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.delivery_app.domain.store.entity.Store;
import com.example.delivery_app.domain.store.enums.IsOpen;
import com.example.delivery_app.domain.store.enums.StoreStatus;
import com.example.delivery_app.domain.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

/**
 * StoreScheduler는 주기적으로 가게의 영업 상태(IsOpen)를 업데이트하는 역할을 합니다.
 * 매 5분마다 현재 시간을 기준으로
 * 각 가게의 영업 시작(openTime)과 종료(closeTime)을 확인하여
 * 가게의 상태를 OPEN 또는 CLOSED로 변경합니다.
 */
@Component
@RequiredArgsConstructor
public class StoreScheduler {

	private final StoreRepository storeRepository;

	/**
	 * 매 5분마다 실행되어, ACTIVE 상태의 가게들을 조회하고
	 * 현재 시간에 따라 IsOpen 상태(OPEN 또는 CLOSED)를 업데이트합니다.
	 * 가게가 열려야 하는 시간대면 OPEN으로,
	 * 닫혀야 하는 시간대면 CLOSED로 상태를 변경합니다.
	 * 변경이 필요한 가게만 골라 일괄 저장합니다.
	 */
	@Scheduled(cron = "0 */5 * * * *")
	public void updateStoreOpenStatus() {
		LocalTime now = LocalTime.now();
		Page<Store> storePage = storeRepository.findAllByStatus(StoreStatus.ACTIVE, Pageable.unpaged());
		List<Store> stores = storePage.getContent();

		List<Store> storesToUpdate = new ArrayList<>();

		for (Store store : stores) {
			IsOpen currentStatus = store.getIsOpen();
			IsOpen newStatus = (now.isAfter(store.getOpenTime()) && now.isBefore(store.getCloseTime()))
				? IsOpen.OPEN
				: IsOpen.CLOSED;

			if (currentStatus != newStatus) {
				store.updateOpenStatus(newStatus);
				storesToUpdate.add(store);
			}
		}

		if (!storesToUpdate.isEmpty()) {
			storeRepository.saveAll(storesToUpdate);
		}
	}
}
