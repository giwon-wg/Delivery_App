package com.example.delivery_app.domain.store.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.delivery_app.domain.store.dto.StoreRequestDto;
import com.example.delivery_app.domain.store.dto.StoreResponseDto;

import jakarta.validation.Valid;

public interface StoreService {

	StoreResponseDto saveStore(StoreRequestDto storeRequestDto);

	StoreResponseDto getPostById(Long storeId);

	Page<StoreResponseDto> getAllStoreList(Pageable pageable);

	StoreResponseDto updateStore(Long storeId, @Valid StoreRequestDto storeRequestDto);
}
