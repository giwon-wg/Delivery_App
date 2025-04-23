package com.example.delivery_app.domain.store.controller;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.delivery_app.domain.store.dto.StoreRequestDto;
import com.example.delivery_app.domain.store.dto.StoreResponseDto;
import com.example.delivery_app.domain.store.entity.Store;
import com.example.delivery_app.domain.store.repository.StoreRepository;

import jakarta.persistence.EntityListeners;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/stores")
@EntityListeners(AuditingEntityListener.class)
@RequiredArgsConstructor
public class StoreController {

	private final StoreRepository storeRepository;

	@PostMapping
	public ResponseEntity<StoreResponseDto> saveStore(
		@Valid @RequestBody StoreRequestDto storeRequestDto
	) {
		Store store = Store.builder()
			.storeName(storeRequestDto.getStoreName())
			.storeAddress(storeRequestDto.getStoreAddress())
			.storeIntro(storeRequestDto.getStoreIntro())
			.storePhone(storeRequestDto.getStorePhone())
			.foodCategory(storeRequestDto.getFoodCategory())
			.minDeliveryPrice(storeRequestDto.getMinDeliveryPrice())
			.deliveryTip(storeRequestDto.getDeliveryTip())
			.build();
		Store savedStore = storeRepository.save(store);
		return ResponseEntity.ok(StoreResponseDto.fromStore(savedStore));
	}
}
