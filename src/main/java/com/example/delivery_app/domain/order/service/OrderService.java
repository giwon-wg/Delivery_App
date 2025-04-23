package com.example.delivery_app.domain.order.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.delivery_app.domain.order.dto.request.OrderRequestDto;
import com.example.delivery_app.domain.order.dto.response.OrderResponseDto;
import com.example.delivery_app.domain.order.entity.Order;
import com.example.delivery_app.domain.order.entity.OrderStatus;
import com.example.delivery_app.domain.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
	private final OrderRepository orderRepository;

	/**
	 * [Service] 주문내역 리스트 조회 메서드
	 * @return 주문 응답 DTO 리스트로 반환
	 */
	@Transactional(readOnly = true)
	public List<OrderResponseDto> findAllOrders() {
		return orderRepository.findAll().stream().map(
			order -> OrderResponseDto.builder()
				.user(order.getUser())
				.store(order.getStore())
				.menu(order.getMenu())
				.status(order.getStatus())
				.build()
		).toList();
	}

	/**
	 * [Service] 주문내역 단건 조회 메서드
	 * @param orderId 주문 id
	 * @return 주문 응답 DTO 반환
	 */
	@Transactional(readOnly = true)
	public OrderResponseDto findById(Long orderId) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new RuntimeException("주문내역이 없습니다."));
		return OrderResponseDto.builder()
			.user(order.getUser())
			.menu(order.getMenu())
			.store(order.getStore())
			.status(order.getStatus())
			.build();
	}

	/**
	 * [Service] 주문 생성(요청) 메서드
	 * @param requestDto 주문 요청 DTO
	 */
	@Transactional
	public void sendOrder(OrderRequestDto requestDto) {
		// FIXME: Store, Menu, User 정보로 Order 객체 생성해야 함
	}

	/**
	 * [Service] 주문 상태 변경 메서드
	 * @param orderId 주문 id
	 * @param status 주문 상태
	 * @return 주문 응답 DTO 반환
	 */
	@Transactional
	public OrderResponseDto requestOrder(Long orderId, OrderStatus status) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new RuntimeException("주문내역이 없습니다."));
		order.setOrderStatus(status);

		return OrderResponseDto.builder()
			.user(order.getUser())
			.menu(order.getMenu())
			.store(order.getStore())
			.status(order.getStatus())
			.build();
	}
}
