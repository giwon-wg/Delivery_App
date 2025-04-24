package com.example.delivery_app.domain.store.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.delivery_app.common.exception.CustomException;
import com.example.delivery_app.domain.store.dto.request.StoreOperatingTimeRequestDto;
import com.example.delivery_app.domain.store.dto.request.StoreRequestDto;
import com.example.delivery_app.domain.store.dto.response.StoreDeleteResponseDto;
import com.example.delivery_app.domain.store.dto.response.StoreResponseDto;
import com.example.delivery_app.domain.store.entity.Store;
import com.example.delivery_app.domain.store.enums.StoreStatus;
import com.example.delivery_app.domain.store.exception.StoreErrorCode;
import com.example.delivery_app.domain.store.repository.StoreRepository;
import com.example.delivery_app.domain.user.Auth.UserAuth;
import com.example.delivery_app.domain.user.entity.User;
import com.example.delivery_app.domain.user.entity.UserRole;
import com.example.delivery_app.domain.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

	private final StoreRepository storeRepository;
	private final UserRepository userRepository;

	@Transactional
	@Override
	public StoreResponseDto saveStore(StoreRequestDto storeRequestDto, UserAuth userAuth) {
		Long userId = userAuth.getId();

		long activeStoreCount = storeRepository.countActiveStoresByUserId(userId);
		if (activeStoreCount >= 3) {
			throw new CustomException(StoreErrorCode.STORE_LIMIT_EXCEEDED);
		}

		User user = userRepository.findById(userAuth.getId())
			.orElseThrow(() -> new CustomException(StoreErrorCode.USER_NOT_FOUND));

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
			.user(user)
			.build();
		Store savedStore = storeRepository.save(store);
		return StoreResponseDto.fromStore(savedStore);
	}

	@Override
	public StoreResponseDto getPostById(Long storeId) {
		Store store = storeRepository.findByIdAndStatusWithMenus(storeId, StoreStatus.ACTIVE)
			.orElseThrow(() -> new CustomException(StoreErrorCode.STORE_NOT_FOUND));

		return StoreResponseDto.fromStore(store);
	}

	@Override
	public Page<StoreResponseDto> getAllStoreList(Pageable pageable) {
		return storeRepository.findAllByStatus(StoreStatus.ACTIVE, pageable).map(StoreResponseDto::fromStore);
	}

	@Transactional
	@Override
	public StoreResponseDto updateStore(Long storeId, StoreRequestDto storeRequestDto, UserAuth userAuth) {
		UserRole role = extractHighestPriorityRole(userAuth);
		Store store = storeRepository.findByIdAndStatusWithMenus(storeId, StoreStatus.ACTIVE)
			.orElseThrow(() -> new CustomException(StoreErrorCode.STORE_NOT_FOUND));

		validateStoreOwner(userAuth.getId(), role, store);
		store.updateStoreInfo(storeRequestDto);
		return StoreResponseDto.fromStore(store);
	}

	@Override
	public StoreDeleteResponseDto deleteStore(Long storeId, UserAuth userAuth) {
		UserRole role = extractHighestPriorityRole(userAuth);
		Store store = storeRepository.findByIdAndStatusWithMenus(storeId, StoreStatus.ACTIVE)
			.orElseThrow(() -> new CustomException(StoreErrorCode.STORE_NOT_FOUND));
		validateStoreOwner(userAuth.getId(), role, store);

		store.markAsInactive();
		storeRepository.save(store);
		return new StoreDeleteResponseDto(store.getStoreId());
	}

	@Transactional
	@Override
	public StoreResponseDto updateOperatingTime(Long storeId, StoreOperatingTimeRequestDto dto, UserAuth userAuth) {
		UserRole role = extractHighestPriorityRole(userAuth);
		Store store = storeRepository.findByIdAndStatusWithMenus(storeId, StoreStatus.ACTIVE)
			.orElseThrow(() -> new CustomException(StoreErrorCode.STORE_NOT_FOUND));
		validateStoreOwner(userAuth.getId(), role, store);

		store.updateOperatingTime(dto.getOpenTime(), dto.getCloseTime());
		return StoreResponseDto.fromStore(store);

	}

	private void validateStoreOwner(Long userId, UserRole role, Store store) {
		if (role == UserRole.ADMIN) {
			return;
		}

		if (!store.getUser().getId().equals(userId)) {
			throw new CustomException(StoreErrorCode.NOT_STORE_OWNER);
		}
	}

	private UserRole extractHighestPriorityRole(UserAuth userAuth) {
		if (userAuth.hasRole("ADMIN")) {
			return UserRole.ADMIN;
		}
		if (userAuth.hasRole("OWNER")) {
			return UserRole.OWNER;
		}
		return UserRole.USER;
	}
}
