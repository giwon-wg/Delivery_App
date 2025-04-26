package com.example.delivery_app.domain.order.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.delivery_app.common.exception.CustomException;
import com.example.delivery_app.domain.order.entity.Order;
import com.example.delivery_app.domain.order.entity.OrderStatus;
import com.example.delivery_app.domain.order.exception.OrderErrorCode;
import com.example.delivery_app.domain.store.entity.Store;
import com.example.delivery_app.domain.store.enums.IsOpen;
import com.example.delivery_app.domain.store.enums.StoreStatus;
import com.example.delivery_app.domain.store.repository.StoreRepository;
import com.example.delivery_app.domain.user.Auth.UserAuth;
import com.example.delivery_app.domain.user.entity.User;
import com.example.delivery_app.domain.user.entity.UserRole;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class OrderServiceValidTest {

	@Mock
	private StoreRepository storeRepository;
	@InjectMocks
	private OrderService orderService;

	@DisplayName("가게가 존재하는지 확인합니다.")
	@Test
	void existStore_success() {
		// given
		Long storeId = 1L;
		when(storeRepository.existsById(storeId)).thenReturn(true);

		// when & then
		assertDoesNotThrow(() ->
			ReflectionTestUtils
				.invokeMethod(
					orderService,
					"existStore", storeId
				)
		);
	}

	@DisplayName("")
	@Test
	void existStore_NotExisted() {
		// given
		Long storeId = 1L;
		when(storeRepository.existsById(storeId)).thenReturn(false);

		// when
		CustomException e = assertThrows(CustomException.class,
			() -> ReflectionTestUtils
				.invokeMethod(
					orderService,
					"existStore", storeId
				)
		);

		// then
		assertEquals(OrderErrorCode.ORDER_IN_STORE_NOT_FOUND, e.getResponseCode());
	}

	@DisplayName("가게가 오픈 상태이고, 폐업하지 않았으며 최소주문금액을 넘기는 주문이 들어왔다.")
	@Test
	void validateStoreAndMinPrice() {
		//given
		Store store = mock(Store.class);
		when(store.getIsOpen()).thenReturn(IsOpen.OPEN);
		when(store.getStatus()).thenReturn(StoreStatus.ACTIVE);
		when(store.getMinDeliveryPrice()).thenReturn(5000);

		//when & then
		assertDoesNotThrow(() ->
			ReflectionTestUtils
				.invokeMethod(
					orderService,
					"validateStoreAndMinPrice", store, 12000
				)
		);
	}

	@DisplayName("가게가 오픈상태가 아니면 예외가 발생한다")
	@Test
	void validateStoreAndMinPrice_storeClosedException() {
		//given
		Store store = mock(Store.class);
		when(store.getIsOpen()).thenReturn(IsOpen.CLOSED);

		//when
		CustomException e = assertThrows(CustomException.class,
			() -> ReflectionTestUtils
				.invokeMethod(
					orderService,
					"validateStoreAndMinPrice", store, 12000
				)
		);

		//then
		assertEquals(OrderErrorCode.ORDER_INVALID_STORE, e.getResponseCode());
	}

	@DisplayName("가게가 폐업이면 예외가 발생한다")
	@Test
	void validateStoreAndMinPrice_storeInactiveException() {
		//given
		Store store = mock(Store.class);
		when(store.getStatus()).thenReturn(StoreStatus.INACTIVE);
		when(store.getIsOpen()).thenReturn(IsOpen.OPEN);

		//when
		CustomException e = assertThrows(CustomException.class,
			() -> ReflectionTestUtils
				.invokeMethod(
					orderService,
					"validateStoreAndMinPrice", store, 12000
				)
		);

		//then
		assertEquals(OrderErrorCode.ORDER_INVALID_STORE, e.getResponseCode());
	}

	@DisplayName("메뉴가격이 가게 최소주문 금액에 못미치면 예외가 발생한다.")
	@Test
	void validateStoreAndMinPrice_minPriceException() {
		//given
		Store store = mock(Store.class);
		when(store.getStatus()).thenReturn(StoreStatus.ACTIVE);
		when(store.getIsOpen()).thenReturn(IsOpen.OPEN);
		when(store.getMinDeliveryPrice()).thenReturn(1000);

		//when
		CustomException e = assertThrows(CustomException.class,
			() -> ReflectionTestUtils
				.invokeMethod(
					orderService,
					"validateStoreAndMinPrice", store, 500
				)
		);
		//then
		assertEquals(OrderErrorCode.ORDER_INVALID_MIN_PRICE, e.getResponseCode());
	}

	@DisplayName("주문이 들어왔고 사장님이 주문수락을 합니다.")
	@Test
	void validateOrderStatus_valid1() {
		//given
		Order order = mock(Order.class);
		when(order.getStatus()).thenReturn(OrderStatus.REQUESTED);

		//when & then
		assertDoesNotThrow(() ->
			ReflectionTestUtils
				.invokeMethod(orderService,
					"validateOrderStatus", order, OrderStatus.ACCEPTED
				)
		);
	}

	@DisplayName("배달중인 주문이 배달완료 되었습니다.")
	@Test
	void validateOrderStatus_valid2() {
		//given
		Order order = mock(Order.class);
		when(order.getStatus()).thenReturn(OrderStatus.ACCEPTED);

		//when & then
		assertDoesNotThrow(() ->
			ReflectionTestUtils
				.invokeMethod(orderService,
					"validateOrderStatus", order, OrderStatus.DELIVERED
				)
		);
	}

	@DisplayName("주문이 들어왔지만 사장님이 취소합니다.")
	@Test
	void validateOrderStatus_valid3() {
		//given
		Order order = mock(Order.class);
		when(order.getStatus()).thenReturn(OrderStatus.ACCEPTED);

		//when & then
		assertDoesNotThrow(() ->
			ReflectionTestUtils
				.invokeMethod(orderService,
					"validateOrderStatus", order, OrderStatus.CANCELLED
				)
		);
	}

	@DisplayName("배달중에 고객이 환불요청을 했고, 사장님은 눈물을 머금고 주문취소합니다.")
	@Test
	void validateOrderStatus_valid4() {
		//given
		Order order = mock(Order.class);
		when(order.getStatus()).thenReturn(OrderStatus.ACCEPTED);

		//when & then
		assertDoesNotThrow(() ->
			ReflectionTestUtils
				.invokeMethod(orderService,
					"validateOrderStatus", order, OrderStatus.CANCELLED
				)
		);
	}

	@DisplayName("주문 상태 변경이 중복되면 예외가 발생한다.")
	@Test
	void validateOrderStatus_duplicatedException() {
		//given
		Order order = mock(Order.class);
		when(order.getStatus()).thenReturn(OrderStatus.REQUESTED);

		//when
		CustomException e = assertThrows(CustomException.class,
			() -> ReflectionTestUtils
				.invokeMethod(orderService,
					"validateOrderStatus", order, OrderStatus.REQUESTED
				)
		);

		//then
		assertEquals(OrderErrorCode.ORDER_STATUS_EQUALS, e.getResponseCode());
	}

	@DisplayName("주문접수 상태의 주문은 배달완료 할 수 없다.")
	@Test
	void validateOrderStatus_NotAccepted() {
		//given
		Order order = mock(Order.class);
		when(order.getStatus()).thenReturn(OrderStatus.REQUESTED);

		//when
		CustomException e = assertThrows(CustomException.class,
			() -> ReflectionTestUtils
				.invokeMethod(orderService,
					"validateOrderStatus", order, OrderStatus.DELIVERED
				)
		);

		//then
		assertEquals(OrderErrorCode.ORDER_STATUS_NOT_ACCEPTED, e.getResponseCode());
	}

	@DisplayName("배달중인 주문은 다시 주문접수상태가 될 수 없다.")
	@Test
	void validateOrderStatus_AlreadyAccepted() {
		//given
		Order order = mock(Order.class);
		when(order.getStatus()).thenReturn(OrderStatus.ACCEPTED);

		//when
		CustomException e = assertThrows(CustomException.class,
			() -> ReflectionTestUtils
				.invokeMethod(orderService,
					"validateOrderStatus", order, OrderStatus.REQUESTED
				)
		);

		//then
		assertEquals(OrderErrorCode.ORDER_STATUS_ALREADY_ACCEPTED, e.getResponseCode());
	}

	@DisplayName("배달완료된 주문은 상태를 변경할 수 없다.")
	@Test
	void validateOrderStatus_AlreadyDelivered() {
		//given
		Order order = mock(Order.class);
		when(order.getStatus()).thenReturn(OrderStatus.DELIVERED);

		//when
		CustomException e = assertThrows(CustomException.class,
			() -> ReflectionTestUtils
				.invokeMethod(orderService,
					"validateOrderStatus", order, OrderStatus.CANCELLED
				)
		);

		//then
		assertEquals(OrderErrorCode.ORDER_STATUS_INVALID, e.getResponseCode());
	}

	@DisplayName("배달취소된 주문은 상태를 변경할 수 없다.")
	@Test
	void validateOrderStatus_AlreadyCanceled() {
		//given
		Order order = mock(Order.class);
		when(order.getStatus()).thenReturn(OrderStatus.CANCELLED);

		//when
		CustomException e = assertThrows(CustomException.class,
			() -> ReflectionTestUtils
				.invokeMethod(orderService,
					"validateOrderStatus", order, OrderStatus.DELIVERED
				)
		);

		//then
		assertEquals(OrderErrorCode.ORDER_STATUS_INVALID, e.getResponseCode());
	}

	@DisplayName("관리자는 모든 주문에 권한이 있다.")
	@Test
	void validateOrderAccess_AccessAdmin() {
		//given
		UserAuth userAuth = mock(UserAuth.class);
		when(userAuth.hasRole(UserRole.ADMIN.name())).thenReturn(true);
		Order order = mock(Order.class);

		//when & then
		assertDoesNotThrow(() ->
			ReflectionTestUtils
				.invokeMethod(
					orderService,
					"validateOrderAccess", userAuth, order
				)
		);
	}

	@DisplayName("사장님은 주문 수락/거절 말고는 권한이 없습니다.")
	@Test
	void validateOrderAccess_forbidOwner() {
		//given
		UserAuth userAuth = mock(UserAuth.class);
		when(userAuth.hasRole(UserRole.OWNER.name())).thenReturn(true);
		when(userAuth.hasRole(UserRole.ADMIN.name())).thenReturn(false);
		Order order = mock(Order.class);

		//when
		CustomException e = assertThrows(CustomException.class,
			() -> ReflectionTestUtils
				.invokeMethod(
					orderService,
					"validateOrderAccess", userAuth, order
				)
		);

		//then
		assertEquals(OrderErrorCode.ORDER_FORBIDDEN, e.getResponseCode());
	}

	@DisplayName("유저가 본인 주문에 접근하면 통과합니다.")
	@Test
	void validateOrderAccess_validUser() {
		//given
		UserAuth userAuth = mock(UserAuth.class);
		Order order = mock(Order.class);
		User user = mock(User.class);

		when(userAuth.hasRole(UserRole.ADMIN.name())).thenReturn(false);
		when(userAuth.hasRole(UserRole.OWNER.name())).thenReturn(false);
		when(order.getUser()).thenReturn(user);
		when(userAuth.getId()).thenReturn(1L);
		when(order.getUser().getId()).thenReturn(1L);

		//when & then
		assertDoesNotThrow(() ->
			ReflectionTestUtils
				.invokeMethod(
					orderService,
					"validateOrderAccess", userAuth, order
				)
		);
	}

	@DisplayName("유저가 본인 주문이 아닌 주문에 접근했을 때 예외처리 합니다.")
	@Test
	void validateOrderAccess_duplicatedUser() {
		//given
		UserAuth userAuth = mock(UserAuth.class);
		Order order = mock(Order.class);
		User user = mock(User.class);

		when(userAuth.hasRole(UserRole.ADMIN.name())).thenReturn(false);
		when(userAuth.hasRole(UserRole.OWNER.name())).thenReturn(false);
		when(order.getUser()).thenReturn(user);
		when(userAuth.getId()).thenReturn(1L);
		when(order.getUser().getId()).thenReturn(2L);

		//when
		CustomException e = assertThrows(CustomException.class,
			() -> ReflectionTestUtils
				.invokeMethod(
					orderService,
					"validateOrderAccess", userAuth, order
				)
		);

		//then
		assertEquals(OrderErrorCode.ORDER_FORBIDDEN, e.getResponseCode());
	}

	@DisplayName("접근을 막고 싶은 권한이 들어오면 막습니다.")
	@Test
	void forbidOrderIfHasRole_valid() {
		//given
		UserAuth userAuth = mock(UserAuth.class);
		when(userAuth.hasRole(UserRole.ADMIN.name())).thenReturn(true);

		//when
		CustomException e = assertThrows(CustomException.class,
			() -> ReflectionTestUtils
				.invokeMethod(
					orderService,
					"forbidOrderIfHasRole", userAuth, UserRole.ADMIN
				)
		);

		//then
		assertEquals(OrderErrorCode.ORDER_FORBIDDEN, e.getResponseCode());
	}
}