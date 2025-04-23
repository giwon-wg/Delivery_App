package com.example.delivery_app.domain.store.service;

import com.example.delivery_app.domain.store.dto.StoreRequestDto;
import com.example.delivery_app.domain.store.dto.StoreResponseDto;

public interface StoreService {

	StoreResponseDto saveStore(StoreRequestDto storeRequestDto);
}
