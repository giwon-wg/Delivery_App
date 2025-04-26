package com.example.delivery_app.domain.order.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.delivery_app.domain.menu.entity.Menu;
import com.example.delivery_app.domain.menu.repository.MenuRepository;
import com.example.delivery_app.domain.order.dto.response.OrderResponseDto;
import com.example.delivery_app.domain.order.entity.Order;
import com.example.delivery_app.domain.order.entity.OrderStatus;
import com.example.delivery_app.domain.order.repository.OrderRepository;
import com.example.delivery_app.domain.store.entity.Store;
import com.example.delivery_app.domain.store.enums.IsOpen;
import com.example.delivery_app.domain.store.enums.StoreStatus;
import com.example.delivery_app.domain.store.repository.StoreRepository;
import com.example.delivery_app.domain.user.Auth.UserAuth;
import com.example.delivery_app.domain.user.entity.User;
import com.example.delivery_app.domain.user.entity.UserRole;
import com.example.delivery_app.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class OrderServiceTest {

	@Mock
	private OrderRepository orderRepository;
	@Mock
	private MenuRepository menuRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private StoreRepository storeRepository;

	@InjectMocks
	private OrderService orderService;

	@DisplayName("주문내역을 조회합니다.")
	@Test
	void findAllOrders() {
		// given
		UserAuth userAuth = mock(UserAuth.class);
		Order order = mock(Order.class);
		User user = mock(User.class);
		Menu menu = mock(Menu.class);
		Store store = mock(Store.class);

		// forbidOrderIfHasRole 메서드
		when(userAuth.hasRole(UserRole.OWNER.name())).thenReturn(false);

		// repository 접근
		when(orderRepository
			.findAllByUserIdAndRole(userAuth.getId(), userAuth.getRoles()))
			.thenReturn(List.of(order));

		// buildOrderResponseDto 메서드
		when(order.getId()).thenReturn(2L);
		when(order.getUser()).thenReturn(user);
		when(order.getMenu()).thenReturn(menu);
		when(order.getStore()).thenReturn(store);
		when(order.getStatus()).thenReturn(OrderStatus.REQUESTED);

		when(user.getId()).thenReturn(3L);
		when(menu.getId()).thenReturn(4L);
		when(store.getStoreId()).thenReturn(5L);

		// when
		List<OrderResponseDto> result = orderService.findAllOrders(userAuth);

		// then
		assertThat(result).hasSize(1);
		OrderResponseDto dto = result.get(0);
		assertThat(dto.getOrderId()).isEqualTo(2L);
		assertThat(dto.getUserId()).isEqualTo(3L);
		assertThat(dto.getMenuId()).isEqualTo(4L);
		assertThat(dto.getStoreId()).isEqualTo(5L);
	}

	@DisplayName("가게정보를 갖고 주문내역을 조회합니다.")
	@Test
	void findAllOrdersByStoreId() {
		// given
		UserAuth userAuth = mock(UserAuth.class);
		Order order = mock(Order.class);
		User user = mock(User.class);
		Menu menu = mock(Menu.class);
		Store store = mock(Store.class);

		// forbidOrderIfHasRole 메서드
		when(userAuth.hasRole(UserRole.USER.name())).thenReturn(false);

		// existStore 메서드
		when(storeRepository.existsById(5L)).thenReturn(true);

		// repository 접근
		when(orderRepository
			.findAllByStoreIdAndRole(5L))
			.thenReturn(List.of(order));

		// buildOrderResponseDto 메서드
		when(order.getId()).thenReturn(2L);
		when(order.getUser()).thenReturn(user);
		when(order.getMenu()).thenReturn(menu);
		when(order.getStore()).thenReturn(store);
		when(order.getStatus()).thenReturn(OrderStatus.REQUESTED);

		when(user.getId()).thenReturn(3L);
		when(menu.getId()).thenReturn(4L);
		when(store.getStoreId()).thenReturn(5L);

		// when
		List<OrderResponseDto> result = orderService.findAllOrdersByStoreId(5L, userAuth);

		// then
		assertThat(result).hasSize(1);
		OrderResponseDto dto = result.get(0);
		assertThat(dto.getOrderId()).isEqualTo(2L);
		assertThat(dto.getUserId()).isEqualTo(3L);
		assertThat(dto.getMenuId()).isEqualTo(4L);
		assertThat(dto.getStoreId()).isEqualTo(5L);
	}

	@DisplayName("주문 단건을 조회합니다.")
	@Test
	void findById_success() {
		// given
		User user = mock(User.class);
		Menu menu = mock(Menu.class);
		Store store = mock(Store.class);
		UserAuth userAuth = mock(UserAuth.class);
		Order order = mock(Order.class);

		// validateOrderAccess 메서드
		when(userAuth.hasRole(UserRole.ADMIN.name())).thenReturn(true);

		// repository 접근
		when(orderRepository.findByIdOrElseThrow(1L)).thenReturn(order);

		// buildOrderResponseDto 메서드
		when(order.getId()).thenReturn(1L);
		when(order.getUser()).thenReturn(user);
		when(order.getMenu()).thenReturn(menu);
		when(order.getStore()).thenReturn(store);
		when(order.getStatus()).thenReturn(OrderStatus.REQUESTED);

		when(user.getId()).thenReturn(2L);
		when(menu.getId()).thenReturn(3L);
		when(store.getStoreId()).thenReturn(4L);

		// when
		OrderResponseDto result = orderService.findById(order.getId(), userAuth);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getOrderId()).isEqualTo(1L);
		assertThat(result.getUserId()).isEqualTo(2L);
		assertThat(result.getMenuId()).isEqualTo(3L);
		assertThat(result.getStoreId()).isEqualTo(4L);
	}

	@DisplayName("메뉴 ID를 받아 주문을 생성합니다.")
	@Test
	void sendOrder_success() {
		// given
		UserAuth userAuth = mock(UserAuth.class);
		User user = mock(User.class);
		Store store = mock(Store.class);
		Menu menu = mock(Menu.class);
		Order order = mock(Order.class);

		// forbidOrderIfHasRole & validateStoreAndMinPrice 메서드
		when(userAuth.hasRole(UserRole.OWNER.name())).thenReturn(false);

		when(menuRepository.findByIdOrElseThrow(1L)).thenReturn(menu);
		when(menu.getStore()).thenReturn(store);

		when(store.getIsOpen()).thenReturn(IsOpen.OPEN);
		when(store.getStatus()).thenReturn(StoreStatus.ACTIVE);
		when(menu.getPrice()).thenReturn(2000);
		when(store.getDeliveryTip()).thenReturn(100);
		when(store.getMinDeliveryPrice()).thenReturn(1000);

		when(userAuth.getId()).thenReturn(2L);
		when(userRepository.findByIdOrElseThrow(userAuth.getId())).thenReturn(user);
		when(orderRepository.save(any(Order.class))).thenReturn(order);

		// buildOrderResponseDto 메서드
		when(order.getId()).thenReturn(3L);
		when(order.getUser()).thenReturn(user);
		when(order.getMenu()).thenReturn(menu);
		when(order.getStore()).thenReturn(store);
		when(order.getStatus()).thenReturn(OrderStatus.REQUESTED);

		when(user.getId()).thenReturn(2L);
		when(menu.getId()).thenReturn(1L);
		when(store.getStoreId()).thenReturn(4L);
		when(menu.getPrice()).thenReturn(2000);

		OrderResponseDto fakeDto = OrderResponseDto.builder()
			.orderId(3L)
			.userId(2L)
			.menuId(1L)
			.storeId(4L)
			.totalPrice(2000)
			.status(OrderStatus.REQUESTED)
			.build();
		ReflectionTestUtils.invokeMethod(orderService, "buildOrderResponseDto", order);

		// when
		OrderResponseDto result = orderService.sendOrder(1L, userAuth);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getMenuId()).isEqualTo(1L);
		assertThat(result.getUserId()).isEqualTo(2L);
		assertThat(result.getStoreId()).isEqualTo(4L);

	}

	@DisplayName("판매자/관리자만 주문을 수락하거나 취소합니다.")
	@Test
	void requestAdminOrder_success() {
		// given
		UserAuth userAuth = mock(UserAuth.class);
		Order order = mock(Order.class);
		User user = mock(User.class);
		Menu menu = mock(Menu.class);
		Store store = mock(Store.class);

		// forbidOrderIfHasRole & validateStoreAndMinPrice 메서드
		when(userAuth.hasRole(UserRole.USER.name())).thenReturn(false);
		when(userAuth.getId()).thenReturn(2L);

		when(orderRepository.findByIdOrElseThrow(1L)).thenReturn(order);
		when(order.getStatus()).thenReturn(OrderStatus.REQUESTED);

		// buildOrderResponseDto 메서드
		when(order.getId()).thenReturn(1L);
		when(order.getUser()).thenReturn(user);
		when(order.getMenu()).thenReturn(menu);
		when(order.getStore()).thenReturn(store);
		when(order.getStatus()).thenReturn(OrderStatus.REQUESTED);

		when(user.getId()).thenReturn(2L);
		when(menu.getId()).thenReturn(3L);
		when(store.getStoreId()).thenReturn(4L);

		// when
		OrderResponseDto result = orderService.requestAdminOrder(1L, OrderStatus.ACCEPTED, userAuth);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getOrderId()).isEqualTo(1L);
		assertThat(result.getUserId()).isEqualTo(2L);
		assertThat(result.getMenuId()).isEqualTo(3L);
		assertThat(result.getStoreId()).isEqualTo(4L);
	}

	@DisplayName("유저/관리자만 주문완료를 요청할 수 있습니다.")
	@Test
	void requestUserOrder_success() {
		// given
		UserAuth userAuth = mock(UserAuth.class);
		Order order = mock(Order.class);
		User user = mock(User.class);
		Menu menu = mock(Menu.class);
		Store store = mock(Store.class);

		// forbidOrderIfHasRole & validateStoreAndMinPrice 메서드
		when(userAuth.hasRole(UserRole.OWNER.name())).thenReturn(false);
		when(userAuth.getId()).thenReturn(2L);

		when(orderRepository.findByIdOrElseThrow(1L)).thenReturn(order);
		when(order.getStatus()).thenReturn(OrderStatus.ACCEPTED);

		// buildOrderResponseDto 메서드
		when(order.getId()).thenReturn(1L);
		when(order.getUser()).thenReturn(user);
		when(order.getMenu()).thenReturn(menu);
		when(order.getStore()).thenReturn(store);
		when(order.getStatus()).thenReturn(OrderStatus.ACCEPTED);

		when(user.getId()).thenReturn(2L);
		when(menu.getId()).thenReturn(3L);
		when(store.getStoreId()).thenReturn(4L);

		// when
		OrderResponseDto result = orderService.requestUserOrder(1L, OrderStatus.DELIVERED, userAuth);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getOrderId()).isEqualTo(1L);
		assertThat(result.getUserId()).isEqualTo(2L);
		assertThat(result.getMenuId()).isEqualTo(3L);
		assertThat(result.getStoreId()).isEqualTo(4L);
	}
}