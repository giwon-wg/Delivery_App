package com.example.delivery_app.domain.order.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.delivery_app.common.exception.CustomException;
import com.example.delivery_app.domain.menu.entity.Menu;
import com.example.delivery_app.domain.menu.repository.MenuRepository;
import com.example.delivery_app.domain.order.dto.response.OrderResponseDto;
import com.example.delivery_app.domain.order.entity.Order;
import com.example.delivery_app.domain.order.entity.OrderStatus;
import com.example.delivery_app.domain.order.exception.OrderErrorCode;
import com.example.delivery_app.domain.order.repository.OrderRepository;
import com.example.delivery_app.domain.store.entity.Store;
import com.example.delivery_app.domain.user.Auth.UserAuth;
import com.example.delivery_app.domain.user.entity.User;
import com.example.delivery_app.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
	private final OrderRepository orderRepository;
	private final MenuRepository menuRepository; // FIXME: 추후 변경
	private final UserRepository userRepository; // FIXME: 추후 변경

	/**
	 * [Service] 주문내역 리스트 조회 메서드
	 * @param userAuth 로그인 객체
	 * @return 주문 응답 DTO 리스트로 반환
	 */
	@Transactional(readOnly = true)
	public List<OrderResponseDto> findAllOrders(UserAuth userAuth) {
		// FIXME: 권한이 없을 경우: 본인(user/owner) 주문내역이 아닐경우 예외처리. 단, ADMIN 이면 가능
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
	 * @param userAuth 로그인 객체
	 * @return 주문 응답 DTO 반환
	 */
	@Transactional(readOnly = true)
	public OrderResponseDto findById(Long orderId, UserAuth userAuth) {
		// FIXME: 권한이 없을 경우: 본인(user/owner) 주문내역이 아닐경우 예외처리. 단, ADMIN 이면 가능
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new CustomException(OrderErrorCode.ORDER_NOT_FOUND));
		return OrderResponseDto.builder()
			.user(order.getUser())
			.menu(order.getMenu())
			.store(order.getStore())
			.status(order.getStatus())
			.build();
	}

	/**
	 * [Service] 주문 생성(요청) 메서드
	 * @param menuId 메뉴 id
	 * @param userAuth 로그인 객체
	 */
	@Transactional
	public OrderResponseDto sendOrder(Long menuId, UserAuth userAuth) {
		// FIXME: 권한이 없을 경우: 본인(user/owner) 주문내역이 아닐경우 예외처리. 단, ADMIN 이면 가능
		// FIXME: 변경해야 하는 로직
		Menu menu = menuRepository.findById(menuId)
			.orElseThrow(() -> new RuntimeException(("존재하지 않는 메뉴입니다.")));
		Store store = menu.getStore();
		User user = userRepository.findById(userAuth.getId())
			.orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));

		Order newOrder = Order.builder()
			.menu(menu)
			.store(store)
			.user(user)
			.build();
		orderRepository.save(newOrder);

		return OrderResponseDto.builder()
			.user(newOrder.getUser())
			.menu(newOrder.getMenu())
			.store(newOrder.getStore())
			.status(newOrder.getStatus())
			.build();
	}

	/**
	 * [Service] 주문 상태 변경 메서드
	 * @param orderId 주문 id
	 * @param status 주문 상태
	 * @param userAuth 로그인 객체
	 * @return 주문 응답 DTO 반환
	 */
	@Transactional
	public OrderResponseDto requestOrder(Long orderId, OrderStatus status, UserAuth userAuth) {
		// FIXME: 권한이 없을 경우: 본인(user/owner) 주문내역이 아닐경우 예외처리. 단, ADMIN 이면 가능
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
