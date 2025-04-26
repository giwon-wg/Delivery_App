package com.example.delivery_app.domain.order.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.delivery_app.common.exception.CustomException;
import com.example.delivery_app.domain.menu.entity.Menu;
import com.example.delivery_app.domain.menu.repository.MenuRepository;
import com.example.delivery_app.domain.order.entity.Order;
import com.example.delivery_app.domain.order.entity.OrderStatus;
import com.example.delivery_app.domain.order.exception.OrderErrorCode;
import com.example.delivery_app.domain.store.entity.Store;
import com.example.delivery_app.domain.store.repository.StoreRepository;
import com.example.delivery_app.domain.user.entity.User;
import com.example.delivery_app.domain.user.entity.UserRole;
import com.example.delivery_app.domain.user.repository.UserRepository;

@DataJpaTest
@ActiveProfiles("test")
class OrderRepositoryTest {

	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private MenuRepository menuRepository;
	@Autowired
	private StoreRepository storeRepository;

	@DisplayName("유효한 주문권한과 가게정보로 주문 목록을 가져온다.")
	@Test
	void findAllByStoreIdAndRole_success() {
		// given
		User owner = User.builder()
			.email("1234@naver.com")
			.isDeleted(false)
			.nickname("user123")
			.password("dkdk1234!")
			.role(UserRole.OWNER)
			.address("address")
			.build();
		Store store = Store.builder()
			.storeName("중국집")
			.storeAddress("address")
			.storeIntro("intro")
			.storePhone("02-2222-2222")
			.openTime(LocalTime.parse("01:00"))
			.closeTime(LocalTime.parse("23:00"))
			.deliveryTip(10)
			.foodCategory("중식")
			.minDeliveryPrice(2000)
			.build();
		userRepository.save(owner);
		storeRepository.save(store);

		Menu menu = Menu.builder()
			.store(store)
			.category("메인")
			.menuPicture("img")
			.menuName("짜장면")
			.price(5000)
			.menuContent("content")
			.build();
		menuRepository.save(menu);

		Order orderByOwner = Order.builder()
			.user(owner).store(store).menu(menu).build();
		orderRepository.save(orderByOwner);

		// when
		List<Order> result = orderRepository.findAllByStoreIdAndRole(store.getStoreId());

		// then
		assertThat(result).hasSize(1)
			.extracting(
				order -> order.getUser().getRole(),
				order -> order.getStore().getStoreName(),
				order -> order.getMenu().getMenuName(),
				Order::getTotalPrice
			)
			.containsExactlyInAnyOrder(
				tuple(owner.getRole(), store.getStoreName(), menu.getMenuName(), orderByOwner.getTotalPrice())
			);
	}

	@Test
	@DisplayName("유효한 유저 권한이면 주문목록을 가져온다.")
	void findAllByUserIdAndRole() {
		// given
		User admin = User.builder()
			.email("123@naver.com")
			.isDeleted(false)
			.nickname("admin123")
			.password("dkdk1234!")
			.role(UserRole.ADMIN)
			.address("address")
			.build();
		User user = User.builder()
			.email("1234@naver.com")
			.isDeleted(false)
			.nickname("user123")
			.password("dkdk1234!")
			.role(UserRole.USER)
			.address("address")
			.build();
		Store store = Store.builder()
			.storeName("중국집")
			.storeAddress("address")
			.storeIntro("intro")
			.storePhone("02-2222-2222")
			.openTime(LocalTime.parse("01:00"))
			.closeTime(LocalTime.parse("23:00"))
			.deliveryTip(10)
			.foodCategory("중식")
			.minDeliveryPrice(2000)
			.build();
		userRepository.saveAll(List.of(admin, user));
		storeRepository.save(store);

		Menu menu1 = Menu.builder()
			.store(store)
			.category("메인")
			.menuPicture("image")
			.menuName("짜장면")
			.price(5000)
			.menuContent("content")
			.build();
		Menu menu2 = Menu.builder()
			.store(store)
			.category("메인")
			.menuPicture("image")
			.menuName("짬봉")
			.price(5000)
			.menuContent("content")
			.build();
		Menu menu3 = Menu.builder()
			.store(store)
			.category("사이드")
			.menuPicture("image")
			.menuName("탕수육")
			.price(10000)
			.menuContent("content")
			.build();
		menuRepository.saveAll(List.of(menu1, menu2, menu3));

		Order orderByAdmin = Order.builder()
			.user(admin).store(store).menu(menu1).build();
		Order orderByUser1 = Order.builder()
			.user(user).store(store).menu(menu2).build();
		Order orderByUser2 = Order.builder()
			.user(user).store(store).menu(menu3).build();
		orderRepository.saveAll(List.of(orderByAdmin, orderByUser1, orderByUser2));

		// when
		List<Order> userOrderList = orderRepository.findAllByUserIdAndRole(user.getId(),
			List.of(user.getRole())); // size 2
		List<Order> adminOrderList = orderRepository.findAllByUserIdAndRole(admin.getId(),
			List.of(admin.getRole())); // size 1

		// then
		assertThat(userOrderList).hasSize(2)
			.extracting(
				order -> order.getUser().getEmail(),
				order -> order.getStore().getStoreName(),
				order -> order.getMenu().getMenuName(),
				Order::getTotalPrice
			)
			.containsExactlyInAnyOrder(
				tuple(user.getEmail(), store.getStoreName(), menu2.getMenuName(), orderByUser1.getTotalPrice()),
				tuple(user.getEmail(), store.getStoreName(), menu3.getMenuName(), orderByUser2.getTotalPrice())
			);
		assertThat(adminOrderList).hasSize(1)
			.extracting(
				order -> order.getUser().getRole(),
				order -> order.getStore().getStoreName(),
				order -> order.getMenu().getMenuName(),
				Order::getTotalPrice
			)
			.containsExactlyInAnyOrder(
				tuple(admin.getRole(), store.getStoreName(), menu1.getMenuName(), orderByAdmin.getTotalPrice())
			);
	}

	@Test
	@DisplayName("유효한 주문 ID를 가지고 주문정보를 가져온다.")
	void findByIdOrElseThrow() {
		// given
		User user = User.builder()
			.email("1234@naver.com")
			.isDeleted(false)
			.nickname("user123")
			.password("dkdk1234!")
			.role(UserRole.USER)
			.address("address")
			.build();
		Store store = Store.builder()
			.storeName("중국집")
			.storeAddress("address")
			.storeIntro("intro")
			.storePhone("02-2222-2222")
			.openTime(LocalTime.parse("01:00"))
			.closeTime(LocalTime.parse("23:00"))
			.deliveryTip(10)
			.foodCategory("중식")
			.minDeliveryPrice(2000)
			.build();
		userRepository.save(user);
		storeRepository.save(store);

		Menu menu = Menu.builder()
			.store(store)
			.category("메인")
			.menuPicture("image")
			.menuName("짜장면")
			.price(5000)
			.menuContent("content")
			.build();
		menuRepository.save(menu);

		Order order = Order.builder()
			.user(user).store(store).menu(menu).build();
		orderRepository.save(order);

		// when
		Order saveOrder = orderRepository.findByIdOrElseThrow(order.getId());

		// then
		assertThat(saveOrder)
			.extracting(
				o -> o.getUser().getEmail(),
				o -> o.getMenu().getMenuName(),
				o -> o.getStore().getStoreName(),
				Order::getStatus
			)
			.containsExactly(
				user.getEmail(),
				menu.getMenuName(),
				store.getStoreName(),
				OrderStatus.REQUESTED
			);

	}

	@DisplayName("존재하지 않는 주문 ID로 조회하면 예외가 발생한다.")
	@Test
	void findByIdOrElseThrow_exception() {
		// given
		Long invalidOrderId = -1L;

		// when & then
		assertThatThrownBy(() -> orderRepository.findByIdOrElseThrow(invalidOrderId))
			.isInstanceOf(CustomException.class)
			.hasMessage(OrderErrorCode.ORDER_NOT_FOUND.getMessage());
	}

}