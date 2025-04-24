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

@Component
@RequiredArgsConstructor
public class StoreScheduler {

	private final StoreRepository storeRepository;

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
