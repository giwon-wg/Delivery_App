package com.example.delivery_app.domain.store.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.delivery_app.domain.store.dto.request.StoreOperatingTimeRequestDto;
import com.example.delivery_app.domain.store.dto.request.StoreRequestDto;
import com.example.delivery_app.domain.store.dto.response.StoreDeleteResponseDto;
import com.example.delivery_app.domain.store.dto.response.StoreGetAllResponseDto;
import com.example.delivery_app.domain.store.dto.response.StoreResponseDto;
import com.example.delivery_app.domain.user.Auth.UserAuth;

/**
 * StoreService는 가게 관련 비즈니스 로직을 정의하는 인터페이스입니다.
 * <p>
 * 가게 생성, 조회, 수정, 삭제, 영업시간 업데이트 기능을 제공합니다.
 */
public interface StoreService {

	/**
	 * 새로운 가게를 등록합니다.
	 *
	 * @param storeRequestDto 등록할 가게 정보
	 * @param userAuth        로그인한 유저 정보
	 * @return 등록된 가게 정보를 반환
	 */
	StoreResponseDto saveStore(StoreRequestDto storeRequestDto, UserAuth userAuth);

	/**
	 * ID로 가게를 조회합니다.
	 *
	 * @param storeId 조회할 가게의 ID
	 * @return 조회된 가게 정보를 반환
	 */
	StoreResponseDto getStoreById(Long storeId);

	/**
	 * 전체 가게 목록을 페이지네이션하여 조회합니다.
	 *
	 * @param pageable 페이징 정보
	 * @return 가게 목록을 포함한 페이지 객체
	 */
	Page<StoreGetAllResponseDto> getAllStoreList(Pageable pageable);

	/**
	 * 가게 정보를 수정합니다.
	 *
	 * @param storeId         수정할 가게의 ID
	 * @param storeRequestDto 수정할 가게 정보
	 * @param userAuth        로그인한 유저 정보
	 * @return 수정된 가게 정보를 반환
	 */
	StoreResponseDto updateStore(Long storeId, StoreRequestDto storeRequestDto, UserAuth userAuth);

	/**
	 * 가게를 삭제(비활성화)합니다.
	 *
	 * @param storeId  삭제할 가게의 ID
	 * @param userAuth 로그인한 유저 정보
	 * @return 삭제 처리 결과를 반환
	 */
	StoreDeleteResponseDto deleteStore(Long storeId, UserAuth userAuth);

	/**
	 * 가게의 영업시간을 수정합니다.
	 *
	 * @param storeId  수정할 가게의 ID
	 * @param dto      수정할 영업시간 정보
	 * @param userAuth 로그인한 유저 정보
	 * @return 수정된 가게 정보를 반환
	 */
	StoreResponseDto updateOperatingTime(Long storeId, StoreOperatingTimeRequestDto dto, UserAuth userAuth);
}
