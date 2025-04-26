package com.example.delivery_app.domain.store.controller;

import static org.springframework.data.domain.Sort.Direction.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.delivery_app.common.dto.CommonResponseDto;
import com.example.delivery_app.domain.store.dto.request.StoreOperatingTimeRequestDto;
import com.example.delivery_app.domain.store.dto.request.StoreRequestDto;
import com.example.delivery_app.domain.store.dto.response.StoreDeleteResponseDto;
import com.example.delivery_app.domain.store.dto.response.StoreGetAllResponseDto;
import com.example.delivery_app.domain.store.dto.response.StoreResponseDto;
import com.example.delivery_app.domain.store.dto.response.StoreSuccessCode;
import com.example.delivery_app.domain.store.service.StoreService;
import com.example.delivery_app.domain.user.Auth.UserAuth;

import jakarta.persistence.EntityListeners;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 가게(Store) 관련 요청을 처리하는 컨트롤러입니다.
 * 가게 등록, 조회, 수정, 삭제 등의 기능을 제공합니다.
 */
@RestController
@RequestMapping("/api/stores")
@EntityListeners(AuditingEntityListener.class)
@RequiredArgsConstructor
public class StoreController {

	private final StoreService storeService;

	/**
	 * @param storeRequestDto 등록할 가게 정보
	 * @param userAuth        인증된 사용자 정보 (사장님)
	 * @return 등록된 가게 정보와 성공 응답
	 * - @PreAuthorize("hasAnyRole('OWNER')"): OWNER 권한을 가진 사용자만 접근 가능합니다.
	 */
	@PreAuthorize("hasAnyRole('OWNER')")
	@PostMapping
	public ResponseEntity<CommonResponseDto<StoreResponseDto>> saveStore(
		@Valid @RequestBody StoreRequestDto storeRequestDto,
		@AuthenticationPrincipal UserAuth userAuth
	) {
		StoreResponseDto savedStore = storeService.saveStore(storeRequestDto, userAuth);
		return ResponseEntity.ok(CommonResponseDto.of(StoreSuccessCode.STORE_ADD_SUCCESS, savedStore));
	}

	/**
	 * 특정 ID를 가진 가게 정보를 조회합니다.
	 *
	 * @param storeId 조회할 가게 ID
	 * @return 가게 정보와 성공 응답
	 */
	@GetMapping("/{storeId}")
	public ResponseEntity<CommonResponseDto<StoreResponseDto>> getStoreById(@PathVariable Long storeId) {
		StoreResponseDto store = storeService.getStoreById(storeId);
		return ResponseEntity.ok(CommonResponseDto.of(StoreSuccessCode.STORE_GET_BY_ID_SUCCESS, store));
	}

	/**
	 * 전체 가게 목록을 페이징 처리하여 조회합니다.
	 *
	 * @param pageable 페이지 정보(size, page, sort 등)
	 * @return 전체 가게 리스트 (페이징 포함)와 성공 응답
	 */
	@GetMapping
	public ResponseEntity<CommonResponseDto<Page<StoreGetAllResponseDto>>> getAllStores(
		@PageableDefault(size = 10, direction = DESC) Pageable pageable) {
		Page<StoreGetAllResponseDto> stores = storeService.getAllStoreList(pageable);
		return ResponseEntity.ok(CommonResponseDto.of(StoreSuccessCode.STORE_GET_ALL_SUCCESS, stores));
	}

	/**
	 * 기존 가게 정보를 수정합니다.
	 *
	 * @param storeId         수정할 가게 ID
	 * @param storeRequestDto 수정할 내용
	 * @param userAuth        인증된 사용자 정보 (사장님 또는 관리자)
	 * @return 수정된 가게 정보와 성공 응답
	 * - @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')"): ADMIN 또는 OWNER 권한 필요
	 */
	@PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
	@PutMapping("/{storeId}")
	public ResponseEntity<CommonResponseDto<StoreResponseDto>> updateStore(
		@PathVariable Long storeId,
		@Valid @RequestBody StoreRequestDto storeRequestDto,
		@AuthenticationPrincipal UserAuth userAuth) {
		StoreResponseDto updatedStore = storeService.updateStore(storeId, storeRequestDto, userAuth);
		return ResponseEntity.ok(CommonResponseDto.of(StoreSuccessCode.STORE_UPDATE_SUCCESS, updatedStore));
	}

	/**
	 * 가게를 비활성화(소프트 딜리트) 처리합니다.
	 *
	 * @param storeId  비활성화할 가게 ID
	 * @param userAuth 인증된 사용자 정보
	 * @return 비활성화된 가게 정보와 성공 응답
	 * - @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')"): 권한 체크
	 */
	@PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
	@DeleteMapping("/{storeId}")
	public ResponseEntity<CommonResponseDto<StoreDeleteResponseDto>> deleteStore(@PathVariable Long storeId,
		@AuthenticationPrincipal UserAuth userAuth) {
		StoreDeleteResponseDto deletedStore = storeService.deleteStore(storeId, userAuth);
		return ResponseEntity.ok(CommonResponseDto.of(StoreSuccessCode.STORE_INACTIVE_SUCCESS, deletedStore));
	}

	/**
	 * 가게의 영업 시작/종료 시간을 업데이트합니다.
	 *
	 * @param storeId  가게 ID
	 * @param dto      영업시간 요청 DTO
	 * @param userAuth 인증된 사용자 정보
	 * @return 변경된 가게 정보와 성공 응답
	 * - @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')"): 권한 확인
	 */
	@PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
	@PutMapping("/{storeId}/operating-time")
	public ResponseEntity<CommonResponseDto<StoreResponseDto>> updateOperatingTime(
		@PathVariable Long storeId,
		@RequestBody StoreOperatingTimeRequestDto dto,
		@AuthenticationPrincipal UserAuth userAuth
	) {
		StoreResponseDto updatedDto = storeService.updateOperatingTime(storeId, dto, userAuth);
		return ResponseEntity.ok(CommonResponseDto.of(StoreSuccessCode.STORE_OP_TIME_UPDATE_SUCCESS, updatedDto));
	}

}
