package com.example.delivery_app.domain.store.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.delivery_app.domain.store.dto.request.StoreOperatingTimeRequestDto;
import com.example.delivery_app.domain.store.dto.request.StoreRequestDto;
import com.example.delivery_app.domain.store.dto.response.StoreDeleteResponseDto;
import com.example.delivery_app.domain.store.dto.response.StoreResponseDto;
import com.example.delivery_app.domain.store.entity.Store;
import com.example.delivery_app.domain.store.enums.StoreStatus;
import com.example.delivery_app.domain.store.repository.StoreRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

	private final StoreRepository storeRepository;

	@Transactional
	@Override
	public StoreResponseDto saveStore(StoreRequestDto storeRequestDto) {
		Store store = Store.builder()
			.storeName(storeRequestDto.getStoreName())
			.storeAddress(storeRequestDto.getStoreAddress())
			.storeIntro(storeRequestDto.getStoreIntro())
			.storePhone(storeRequestDto.getStorePhone())
			.foodCategory(storeRequestDto.getFoodCategory())
			.minDeliveryPrice(storeRequestDto.getMinDeliveryPrice())
			.deliveryTip(storeRequestDto.getDeliveryTip())
			.openTime(storeRequestDto.getOpenTime())
			.closeTime(storeRequestDto.getCloseTime())
			.build();
		Store savedStore = storeRepository.save(store);
		return StoreResponseDto.fromStore(savedStore);
	}

	@Override
	public StoreResponseDto getPostById(Long storeId) {
		Store store = storeRepository.findByIdAndStatus(storeId, StoreStatus.ACTIVE)
			.orElseThrow(() -> new IllegalArgumentException("해당 가게가 존재하지 않습니다. id=" + storeId));

		return StoreResponseDto.fromStore(store);
	}

	@Override
	public Page<StoreResponseDto> getAllStoreList(Pageable pageable) {
		return storeRepository.findAllByStatus(StoreStatus.ACTIVE, pageable).map(StoreResponseDto::fromStore);
	}

	@Transactional
	@Override
	public StoreResponseDto updateStore(Long storeId, StoreRequestDto storeRequestDto) {
		Store store = storeRepository.findByIdAndStatus(storeId, StoreStatus.ACTIVE)
			.orElseThrow(() -> new IllegalArgumentException("가게가 없습니다."));
		store.updateStoreInfo(storeRequestDto);
		return StoreResponseDto.fromStore(store);
	}

	@Override
	public StoreDeleteResponseDto deleteStore(Long storeId) {
		Store store = storeRepository.findByIdAndStatus(storeId, StoreStatus.ACTIVE)
			.orElseThrow(() -> new IllegalArgumentException("가게가 없습니다."));

		store.markAsInactive();
		storeRepository.save(store);
		return new StoreDeleteResponseDto(store.getStoreId());
	}

	@Transactional
	@Override
	public void updateOperatingTime(Long storeId, StoreOperatingTimeRequestDto dto) {
		Store store = storeRepository.findByIdAndStatus(storeId, StoreStatus.ACTIVE)
			.orElseThrow(() -> new IllegalArgumentException("가게가 없습니다"));
		log.info(">> DTO openTime = {}", dto.getOpenTime());    // 꼭 찍어보세요
		log.info(">> DTO closeTime = {}", dto.getCloseTime());  // 여기가 null이면 DTO 바인딩 문제

		store.updateOperatingTime(dto.getOpenTime(), dto.getCloseTime());
	}
}