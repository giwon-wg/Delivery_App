package com.example.delivery_app.domain.store.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.delivery_app.common.exception.CustomException;
import com.example.delivery_app.domain.store.dto.request.StoreOperatingTimeRequestDto;
import com.example.delivery_app.domain.store.dto.request.StoreRequestDto;
import com.example.delivery_app.domain.store.dto.response.StoreDeleteResponseDto;
import com.example.delivery_app.domain.store.dto.response.StoreGetAllResponseDto;
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

/**
 * StoreServiceImpl은 StoreService의 구현체로, 가게 등록, 조회, 수정, 삭제, 영업시간 변경 기능을 담당합니다.
 * <p>
 * 유저 인증 정보를 기반으로 가게에 대한 권한을 검증하고,
 * 필요한 경우 커스텀 예외를 발생시킵니다.
 */
@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

	private final StoreRepository storeRepository;
	private final UserRepository userRepository;

	/**
	 * 새로운 가게를 등록합니다.
	 * 운영 가능한 가게 수가 3개를 초과하는 경우 예외를 발생시킵니다.
	 */
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

	/**
	 * 가게 ID로 가게를 조회합니다.
	 * 조회된 가게가 없거나 비활성화된 경우 예외를 발생시킵니다.
	 */
	@Transactional
	@Override
	public StoreResponseDto getStoreById(Long storeId) {
		Store store = storeRepository.findByIdAndStatusWithMenus(storeId, StoreStatus.ACTIVE)
			.orElseThrow(() -> new CustomException(StoreErrorCode.STORE_NOT_FOUND));

		return StoreResponseDto.fromStore(store);
	}

	/**
	 * ACTIVE 상태의 전체 가게 목록을 페이지네이션하여 조회합니다.
	 */
	@Override
	public Page<StoreGetAllResponseDto> getAllStoreList(Pageable pageable) {
		return storeRepository.findAllByStatus(StoreStatus.ACTIVE, pageable).map(StoreGetAllResponseDto::fromStore);
	}

	/**
	 * 가게 정보를 수정합니다.
	 * 유저 권한을 검증하여 소유주 또는 관리자인 경우에만 수정할 수 있습니다.
	 */
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

	/**
	 * 가게를 비활성화(삭제)합니다.
	 * 유저 권한을 검증하여 소유주 또는 관리자인 경우에만 삭제할 수 있습니다.
	 */
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

	/**
	 * 가게의 영업시간을 수정합니다.
	 * 유저 권한을 검증하여 소유주 또는 관리자인 경우에만 수정할 수 있습니다.
	 */
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

	/**
	 * 유저가 해당 가게의 소유주가 아니거나 관리자가 아닌 경우 예외를 발생시킵니다.
	 */
	private void validateStoreOwner(Long userId, UserRole role, Store store) {
		if (role == UserRole.ADMIN) {
			return;
		}

		if (!store.getUser().getId().equals(userId)) {
			throw new CustomException(StoreErrorCode.NOT_STORE_OWNER);
		}
	}

	/**
	 * UserAuth 정보에서 가장 높은 우선순위의 역할(Role)을 추출합니다.
	 */
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
