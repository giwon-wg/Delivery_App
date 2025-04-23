package com.example.delivery_app.domain.store.controller;

import static org.springframework.data.domain.Sort.Direction.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.delivery_app.domain.store.dto.StoreRequestDto;
import com.example.delivery_app.domain.store.dto.StoreResponseDto;
import com.example.delivery_app.domain.store.service.StoreService;

import jakarta.persistence.EntityListeners;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stores")
@EntityListeners(AuditingEntityListener.class)
@RequiredArgsConstructor
public class StoreController {

	private final StoreService storeService;

	@PostMapping
	public ResponseEntity<StoreResponseDto> saveStore(
		@Valid @RequestBody StoreRequestDto storeRequestDto
	) {
		StoreResponseDto savedStore = storeService.saveStore(storeRequestDto);
		return ResponseEntity.ok(savedStore);
	}

	@GetMapping("/{storeId}")
	public ResponseEntity<StoreResponseDto> getStorebyId(@PathVariable Long storeId) {
		StoreResponseDto store = storeService.getPostById(storeId);
		return ResponseEntity.ok(store);
	}

	public ResponseEntity<Page<StoreResponseDto>> getAllStores(
		@PageableDefault(size = 10, direction = DESC) Pageable pageable) {
		Page<StoreResponseDto> stores = storeService.getAllStoreList(pageable);
		return ResponseEntity.ok(stores);
	}

}
