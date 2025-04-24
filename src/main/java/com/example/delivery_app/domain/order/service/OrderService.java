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
import com.example.delivery_app.domain.user.entity.UserRole;
import com.example.delivery_app.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
	private final OrderRepository orderRepository;
	private final MenuRepository menuRepository;
	private final UserRepository userRepository;

	/**
	 * [Service] 주문내역 리스트 조회 메서드
	 * @param userAuth 로그인 객체
	 * @return 주문 응답 DTO 리스트로 반환
	 */
	@Transactional(readOnly = true)
	public List<OrderResponseDto> findAllOrders(UserAuth userAuth) {
		forbidOrderIfHasRole(userAuth, UserRole.OWNER);
		return orderRepository.findAllByUserIdAndRole(
				userAuth.getId(), userAuth.getRoles())
			.stream()
			.map(
				this::buildOrderResponseDto
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
		Order order = orderRepository.findByIdOrElseThrow(orderId);
		validateOrderAccess(userAuth, order);

		return buildOrderResponseDto(order);
	}

	/**
	 * [Service] 주문 생성(요청) 메서드
	 * @param menuId 메뉴 id
	 * @param userAuth 로그인 객체
	 */
	@Transactional
	public OrderResponseDto sendOrder(Long menuId, UserAuth userAuth) {
		forbidOrderIfHasRole(userAuth, UserRole.OWNER);

		Menu menu = menuRepository.findByIdOrElseThrow(menuId);
		Store store = menu.getStore();
		User user = userRepository.findByIdOrElseThrow(userAuth.getId());

		Order newOrder = Order.builder()
			.menu(menu)
			.store(store)
			.user(user)
			.status(OrderStatus.REQUESTED)
			.build();
		orderRepository.save(newOrder);
		return buildOrderResponseDto(newOrder);
	}

	/**
	 * [Service] 관리자 주문 상태 변경 메서드
	 * @param orderId 주문 id
	 * @param status 주문 상태
	 * @param userAuth 로그인 객체
	 * @return 주문 응답 DTO 반환
	 */
	@Transactional
	public OrderResponseDto requestAdminOrder(Long orderId, OrderStatus status, UserAuth userAuth) {
		forbidOrderIfHasRole(userAuth, UserRole.USER);

		Order order = orderRepository.findByIdOrElseThrow(orderId);
		order.setOrderStatus(status);
		return buildOrderResponseDto(order);
	}

	/**
	 * [Service] 유저 주문 상태 변경 메서드
	 * @param orderId 주문 id
	 * @param status 주문 상태
	 * @param userAuth 로그인 객체
	 * @return 주문 응답 DTO 반환
	 */
	@Transactional
	public OrderResponseDto requestUserOrder(Long orderId, OrderStatus status, UserAuth userAuth) {
		forbidOrderIfHasRole(userAuth, UserRole.ADMIN);

		Order order = orderRepository.findByIdOrElseThrow(orderId);
		order.setOrderStatus(status);
		return buildOrderResponseDto(order);
	}

	/**
	 * OrderResponseDto 를 빌더로 생성하여 반환하는 메서드
	 * @param order 주문 객체
	 * @return OrderResponseDto 반환
	 */
	private OrderResponseDto buildOrderResponseDto(Order order) {
		return OrderResponseDto.builder()
			.user(order.getUser())
			.menu(order.getMenu())
			.store(order.getStore())
			.status(order.getStatus())
			.build();
	}

	/**
	 * 특정 권한이 있으면 접근을 제한하는 메서드
	 * @param userAuth 로그인 유저의 권한이 담긴 객체
	 * @param forbidRole 해당 권한이 있으면 예외 처리
	 */
	private void forbidOrderIfHasRole(UserAuth userAuth, UserRole forbidRole) {
		if (userAuth.hasRole(forbidRole)) {
			throw new CustomException(OrderErrorCode.ORDER_FORBIDDEN);
		}
	}

	/**
	 * 로그인 유저가 주문에 권한이 있는지 검증하는 메서드
	 * 관리자는 모든 주문에 접근 가능
	 * 유저의 경우, 본인의 주문에만 접근 가능
	 * 사장님의 경우, 모든 주문에 접근 불가
	 * @param userAuth 로그인 유저의 권한이 담긴 객체
	 * @param order 접근하고자 하는 주문 객체
	 */
	private void validateOrderAccess(UserAuth userAuth, Order order) {
		// 관리자는 모든 주문에 접근 가능
		if (userAuth.hasRole(UserRole.ADMIN))
			return;

		// 사장님은 모든 주문에 접근 불가능
		forbidOrderIfHasRole(userAuth, UserRole.OWNER);

		// 유저의 경우, 본인 주문이 아니면 예외 처리
		if (!order.getUser().getId().equals(userAuth.getId())) {
			throw new CustomException(OrderErrorCode.ORDER_FORBIDDEN);
		}
	}
}
