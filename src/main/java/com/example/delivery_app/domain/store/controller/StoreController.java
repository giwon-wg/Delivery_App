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

@RestController
@RequestMapping("/api/stores")
@EntityListeners(AuditingEntityListener.class)
@RequiredArgsConstructor
public class StoreController {

	private final StoreService storeService;

	@PreAuthorize("hasAnyRole('OWNER')")
	@PostMapping
	public ResponseEntity<CommonResponseDto<StoreResponseDto>> saveStore(
		@Valid @RequestBody StoreRequestDto storeRequestDto,
		@AuthenticationPrincipal UserAuth userAuth
	) {
		StoreResponseDto savedStore = storeService.saveStore(storeRequestDto, userAuth);
		return ResponseEntity.ok(CommonResponseDto.of(StoreSuccessCode.STORE_ADD_SUCCESS, savedStore));
	}

	@GetMapping("/{storeId}")
	public ResponseEntity<CommonResponseDto<StoreResponseDto>> getStoreById(@PathVariable Long storeId) {
		StoreResponseDto store = storeService.getPostById(storeId);
		return ResponseEntity.ok(CommonResponseDto.of(StoreSuccessCode.STORE_GET_BY_ID_SUCCESS, store));
	}

	@GetMapping
	public ResponseEntity<CommonResponseDto<Page<StoreGetAllResponseDto>>> getAllStores(
		@PageableDefault(size = 10, direction = DESC) Pageable pageable) {
		Page<StoreGetAllResponseDto> stores = storeService.getAllStoreList(pageable);
		return ResponseEntity.ok(CommonResponseDto.of(StoreSuccessCode.STORE_GET_ALL_SUCCESS, stores));
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
	@PutMapping("/{storeId}")
	public ResponseEntity<CommonResponseDto<StoreResponseDto>> updateStore(
		@PathVariable Long storeId,
		@Valid @RequestBody StoreRequestDto storeRequestDto,
		@AuthenticationPrincipal UserAuth userAuth) {
		StoreResponseDto updatedStore = storeService.updateStore(storeId, storeRequestDto, userAuth);
		return ResponseEntity.ok(CommonResponseDto.of(StoreSuccessCode.STORE_UPDATE_SUCCESS, updatedStore));
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
	@DeleteMapping("/{storeId}")
	public ResponseEntity<CommonResponseDto<StoreDeleteResponseDto>> deleteStore(@PathVariable Long storeId,
		@AuthenticationPrincipal UserAuth userAuth) {
		StoreDeleteResponseDto deletedStore = storeService.deleteStore(storeId, userAuth);
		return ResponseEntity.ok(CommonResponseDto.of(StoreSuccessCode.STORE_INACTIVE_SUCCESS, deletedStore));
	}

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
