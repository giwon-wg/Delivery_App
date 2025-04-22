package com.example.delivery_app.domain.order.controller;

import static com.example.delivery_app.domain.order.dto.response.SuccessResponseDto.*;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.delivery_app.domain.order.dto.request.OrderRequestDto;
import com.example.delivery_app.domain.order.dto.response.OrderResponseDto;
import com.example.delivery_app.domain.order.dto.response.ResponseCode;
import com.example.delivery_app.domain.order.dto.response.SuccessResponseDto;
import com.example.delivery_app.domain.order.entity.OrderStatus;
import com.example.delivery_app.domain.order.service.OrderService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequestMapping("/api/ordesr")
@RestController
@RequiredArgsConstructor
public class OrderController {
	private final OrderService orderService;

	// FIXME: JWT 토큰에 저장된 유저 id 가져와야 함

	/**
	 * [Controller] 주문내역 리스트를 조회하는 메서드
	 * @param request HTTP 요청 객체
	 * @return 주문 내역 응답 리스트를 반환
	 */
	@GetMapping
	public ResponseEntity<SuccessResponseDto<List<OrderResponseDto>>> getAllOrders(
		HttpServletRequest request
	) {
		return createResponseEntityDto(
			ResponseCode.ORDER_GET_LIST_SUCCESS,
			request.getRequestURI(),
			orderService.findAllOrders()
		);
	}

	/**
	 * [Controller] 주문내역 단일 조회하는 메서드
	 * @param orderId 주문 id
	 * @param request HTTP 요청 객체
	 * @return 주문 내역 응답 DTO 반환
	 */
	@GetMapping("/{orderId}")
	public ResponseEntity<SuccessResponseDto<OrderResponseDto>> getOrder(
		@PathVariable(name = "orderId") Long orderId,
		HttpServletRequest request) {
		return createResponseEntityDto(
			ResponseCode.ORDER_GET_DETAIL_SUCCESS,
			request.getRequestURI(),
			orderService.findById(orderId)
		);
	}

	/**
	 * FIXME: [Controller] 주문 요청(생성)하는 메서드
	 * @param dto 주문 요청 DTO
	 * @param request HTTP 요청 객체
	 */
	@PostMapping
	public void sendOrder(
		@Validated @RequestBody OrderRequestDto dto,
		HttpServletRequest request) {
		orderService.sendOrder(dto);
	}

	/**
	 * [Controller/관리자] 주문 수락하는 메서드
	 * @param orderId 주문 id
	 * @param request HTTP 요청 객체
	 * @return 수락한 주문 응답 객체를 반환
	 */
	@PatchMapping("/{orderId}/accept")
	public ResponseEntity<SuccessResponseDto<OrderResponseDto>> acceptOrder(
		@PathVariable(name = "orderId") Long orderId,
		HttpServletRequest request) {
		return createResponseEntityDto(
			ResponseCode.ORDER_PATCH_ACCEPTED_SUCCESS,
			request.getRequestURI(),
			orderService.requestOrder(orderId, OrderStatus.ACCEPTED)
		);
	}

	/**
	 * [Controller/관리자] 주문 완료하는 메서드
	 * @param orderId 주문 id
	 * @param request HTTP 요청 객체
	 * @return 배달완료한 주문 응답 객체를 반환
	 */
	@PatchMapping("/{orderId}/complete")
	public ResponseEntity<SuccessResponseDto<OrderResponseDto>> completeOrder(
		@PathVariable(name = "orderId") Long orderId,
		HttpServletRequest request) {
		return createResponseEntityDto(
			ResponseCode.ORDER_PATCH_COMPLETED_SUCCESS,
			request.getRequestURI(),
			orderService.requestOrder(orderId, OrderStatus.DELIVERED)
		);
	}

	/**
	 * [Controller/관리자] 주문을 거절하는 메서드
	 * @param storeId 주문 id
	 * @param request HTTP 요청 객체
	 * @return 배달거절한 주문 응답 객체를 반환
	 */
	@PatchMapping("/{storeId}/reject")
	public ResponseEntity<SuccessResponseDto<OrderResponseDto>> rejectOrder(
		@PathVariable(name = "storeId") Long storeId,
		HttpServletRequest request) {
		return createResponseEntityDto(
			ResponseCode.ORDER_PATCH_REJECTED_SUCCESS,
			request.getRequestURI(),
			orderService.requestOrder(storeId, OrderStatus.CANCELLED)
		);
	}
}
