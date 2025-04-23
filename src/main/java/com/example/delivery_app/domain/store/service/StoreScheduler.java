package com.example.delivery_app.domain.store.service;

import java.time.LocalTime;
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

@Component
@RequiredArgsConstructor
public class StoreScheduler {

	private final StoreRepository storeRepository;

	@Scheduled(cron = "0 */1 * * * *")
	public void updateStoreOpenStatus() {
		LocalTime now = LocalTime.now();
		Page<Store> storePage = storeRepository.findAllByStatus(StoreStatus.ACTIVE, Pageable.unpaged());
		List<Store> stores = storePage.getContent();

		for (Store store : stores) {
			if (now.isAfter(store.getOpenTime()) && now.isBefore(store.getCloseTime())) {
				store.updateOpenStatus(IsOpen.OPEN);
			} else {
				store.updateOpenStatus(IsOpen.CLOSED);
			}
		}
		storeRepository.saveAll(stores);
	}
}
