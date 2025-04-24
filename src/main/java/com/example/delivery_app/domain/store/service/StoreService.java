package com.example.delivery_app.domain.store.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.delivery_app.domain.store.dto.request.StoreOperatingTimeRequestDto;
import com.example.delivery_app.domain.store.dto.request.StoreRequestDto;
import com.example.delivery_app.domain.store.dto.response.StoreDeleteResponseDto;
import com.example.delivery_app.domain.store.dto.response.StoreResponseDto;
import com.example.delivery_app.domain.user.Auth.UserAuth;

public interface StoreService {

	StoreResponseDto saveStore(StoreRequestDto storeRequestDto, UserAuth userAuth);

	StoreResponseDto getPostById(Long storeId);

	Page<StoreResponseDto> getAllStoreList(Pageable pageable);

	StoreResponseDto updateStore(Long storeId, StoreRequestDto storeRequestDto, UserAuth userAuth);

	StoreDeleteResponseDto deleteStore(Long storeId, UserAuth userAuth);

	StoreResponseDto updateOperatingTime(Long storeId, StoreOperatingTimeRequestDto dto, UserAuth userAuth);
}
