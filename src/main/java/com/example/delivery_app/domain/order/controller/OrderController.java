package com.example.delivery_app.domain.order.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.delivery_app.common.dto.CommonResponseDto;
import com.example.delivery_app.domain.order.dto.response.OrderResponseDto;
import com.example.delivery_app.domain.order.dto.response.OrderSuccessCode;
import com.example.delivery_app.domain.order.entity.OrderStatus;
import com.example.delivery_app.domain.order.service.OrderService;
import com.example.delivery_app.domain.user.Auth.UserAuth;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/orders")
@RestController
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;

	/**
	 * [Controller] 주문내역 리스트를 조회하는 메서드
	 * @param auth 로그인 객체
	 * @return 주문 내역 응답 리스트를 반환
	 */
	@GetMapping
	public ResponseEntity<CommonResponseDto<List<OrderResponseDto>>> getAllOrders(
		@AuthenticationPrincipal UserAuth auth
	) {
		return ResponseEntity.ok(
			CommonResponseDto.of(
				OrderSuccessCode.ORDER_GET_ALL_SUCCESS,
				orderService.findAllOrders(auth)
			)
		);
	}

	/**
	 * [Controller] 주문내역 단일 조회하는 메서드
	 * @param orderId 주문 id
	 * @param auth 로그인 객체
	 * @return 주문 내역 응답 DTO 반환
	 */
	@GetMapping("/{orderId}")
	public ResponseEntity<CommonResponseDto<OrderResponseDto>> getOrder(
		@PathVariable(name = "orderId") Long orderId,
		@AuthenticationPrincipal UserAuth auth) {
		return ResponseEntity.ok(
			CommonResponseDto.of(
				OrderSuccessCode.ORDER_GET_DETAIL_SUCCESS,
				orderService.findById(orderId, auth)
			)
		);
	}

	/**
	 * [Controller] 주문 요청(생성)하는 메서드
	 * @param menuId 메뉴 id
	 * @param auth 로그인 객체
	 */
	@PostMapping("/{menuId}")
	public ResponseEntity<CommonResponseDto<OrderResponseDto>> sendOrder(
		@PathVariable(name = "menuId") Long menuId,
		@AuthenticationPrincipal UserAuth auth) {
		return ResponseEntity.status(HttpStatus.CREATED).body(
			CommonResponseDto.of(
				OrderSuccessCode.ORDER_CREATE_SUCCESS,
				orderService.sendOrder(menuId, auth)
			)
		);
	}

	/**
	 * [Controller/관리자] 주문 수락하는 메서드
	 * @param orderId 주문 id
	 * @param auth 로그인 객체
	 * @return 수락한 주문 응답 객체를 반환
	 */
	@PatchMapping("/{orderId}/accept")
	public ResponseEntity<CommonResponseDto<OrderResponseDto>> acceptOrder(
		@PathVariable(name = "orderId") Long orderId,
		@AuthenticationPrincipal UserAuth auth) {
		return ResponseEntity.ok(
			CommonResponseDto.of(
				OrderSuccessCode.ORDER_ACCEPT_SUCCESS,
				orderService.requestAdminOrder(orderId, OrderStatus.ACCEPTED, auth)
			)
		);
	}

	/**
	 * [Controller/관리자] 주문 완료하는 메서드
	 * @param orderId 주문 id
	 * @param auth 로그인 객체
	 * @return 배달완료한 주문 응답 객체를 반환
	 */
	@PatchMapping("/{orderId}/complete")
	public ResponseEntity<CommonResponseDto<OrderResponseDto>> completeOrder(
		@PathVariable(name = "orderId") Long orderId,
		@AuthenticationPrincipal UserAuth auth) {
		return ResponseEntity.ok(
			CommonResponseDto.of(
				OrderSuccessCode.ORDER_COMPLETE_SUCCESS,
				orderService.requestUserOrder(orderId, OrderStatus.DELIVERED, auth)
			)
		);
	}

	/**
	 * [Controller/관리자] 주문을 거절하는 메서드
	 * @param storeId 주문 id
	 * @param auth 로그인 객체
	 * @return 배달거절한 주문 응답 객체를 반환
	 */
	@PatchMapping("/{storeId}/reject")
	public ResponseEntity<CommonResponseDto<OrderResponseDto>> rejectOrder(
		@PathVariable(name = "storeId") Long storeId,
		@AuthenticationPrincipal UserAuth auth) {
		return ResponseEntity.ok(
			CommonResponseDto.of(
				OrderSuccessCode.ORDER_REJECT_SUCCESS,
				orderService.requestAdminOrder(storeId, OrderStatus.CANCELLED, auth)
			)
		);
	}
}
