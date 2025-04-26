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
import com.example.delivery_app.domain.store.enums.IsOpen;
import com.example.delivery_app.domain.store.enums.StoreStatus;
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
			.map(this::buildOrderResponseDto)
			.toList();
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
		validateStoreAndMinPrice(store, menu.getPrice());

		User user = userRepository.findByIdOrElseThrow(userAuth.getId());

		Order newOrder = Order.builder()
			.menu(menu)
			.store(store)
			.user(user)
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
		validateOrderAccess(userAuth, order);

		validateOrderStatus(order, status);
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
		forbidOrderIfHasRole(userAuth, UserRole.OWNER);

		Order order = orderRepository.findByIdOrElseThrow(orderId);
		validateOrderAccess(userAuth, order);

		validateOrderStatus(order, status);
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
			.orderId(order.getId())
			.userId(order.getUser().getId())
			.menuId(order.getMenu().getId())
			.storeId(order.getStore().getStoreId())
			.totalPrice(order.getMenu().getPrice())
			.status(order.getStatus())
			.build();
	}

	/**
	 * 특정 권한이 있으면 접근을 제한하는 메서드
	 * @param userAuth 로그인 유저의 권한이 담긴 객체
	 * @param forbidRole 해당 권한이 있으면 예외 처리
	 */
	private void forbidOrderIfHasRole(UserAuth userAuth, UserRole forbidRole) {
		if (userAuth.hasRole(forbidRole.name())) {
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
		if (userAuth.hasRole(UserRole.ADMIN.name()))
			return;

		// 사장님은 모든 주문에 접근 불가능
		forbidOrderIfHasRole(userAuth, UserRole.OWNER);

		// 유저의 경우, 본인 주문이 아니면 예외 처리
		if (!userAuth.getId().equals(order.getUser().getId())) {
			throw new CustomException(OrderErrorCode.ORDER_FORBIDDEN);
		}
	}

	/**
	 * 요청한 주문 상태 변경을 검증하는 메서드
	 * @param order 주문 객체
	 * @param requestStatus 요청한 주문 상태 변경 값
	 */
	private void validateOrderStatus(Order order, OrderStatus requestStatus) {
		OrderStatus currentStatus = order.getStatus();
		// 중복 요청은 허용하지 않음
		if (currentStatus == requestStatus) {
			throw new CustomException(OrderErrorCode.ORDER_STATUS_EQUALS);
		}

		// 요청중인 주문은 배달완료할 수 없음 (수락/취소 가능)
		if (currentStatus == OrderStatus.REQUESTED && requestStatus == OrderStatus.DELIVERED) {
			throw new CustomException(OrderErrorCode.ORDER_STATUS_NOT_ACCEPTED);
		}

		// 수락된 주문은 요청할 수 없음 (배달완료/취소 가능)
		if (currentStatus == OrderStatus.ACCEPTED && requestStatus == OrderStatus.REQUESTED) {
			throw new CustomException(OrderErrorCode.ORDER_STATUS_ALREADY_ACCEPTED);
		}

		// 배달완료/취소된 주문은 상태변경 불가능
		if ((currentStatus == OrderStatus.DELIVERED || currentStatus == OrderStatus.CANCELLED) &&
			(requestStatus == OrderStatus.REQUESTED || requestStatus == OrderStatus.CANCELLED
				|| requestStatus == OrderStatus.ACCEPTED || requestStatus == OrderStatus.DELIVERED)) {
			throw new CustomException(OrderErrorCode.ORDER_STATUS_INVALID);
		}
	}

	/**
	 * 가게의 폐업여부와, 운영시간과 최소주문금액을 검증하는 메서드
	 * @param store 가게 객체
	 * @param menuPrice 메뉴 금액
	 */
	private void validateStoreAndMinPrice(Store store, int menuPrice) {
		if (store.getIsOpen().equals(IsOpen.CLOSED) || store.getStatus().equals(StoreStatus.INACTIVE)) {
			throw new CustomException(OrderErrorCode.ORDER_INVALID_STORE);
		}
		if (store.getMinDeliveryPrice() > menuPrice) {
			throw new CustomException(OrderErrorCode.ORDER_INVALID_MIN_PRICE);
		}
	}
}
