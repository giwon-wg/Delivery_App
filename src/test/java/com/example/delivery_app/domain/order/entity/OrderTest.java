package com.example.delivery_app.domain.order.entity;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.delivery_app.domain.menu.dto.requestdto.MenuRequestDto;
import com.example.delivery_app.domain.menu.entity.Menu;
import com.example.delivery_app.domain.menu.repository.MenuRepository;
import com.example.delivery_app.domain.order.repository.OrderRepository;
import com.example.delivery_app.domain.store.entity.Store;
import com.example.delivery_app.domain.store.repository.StoreRepository;
import com.example.delivery_app.domain.user.entity.User;
import com.example.delivery_app.domain.user.entity.UserRole;
import com.example.delivery_app.domain.user.repository.UserRepository;

@DataJpaTest
@ActiveProfiles("test")
class OrderTest {

	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private MenuRepository menuRepository;
	@Autowired
	private StoreRepository storeRepository;

	@DisplayName("주문 생성시에 총 금액을 계산합니다.")
	@Test
	void createOrderAfterSetTotalPrice() {
		// ✅ given
		User user = userRepository.save(User.builder()
			.email("1234@naver.com")
			.isDeleted(false)
			.nickname("user123")
			.password("dkdk1234!")
			.role(UserRole.USER)
			.address("address")
			.build());

		Store store = storeRepository.save(Store.builder()
			.storeName("중국집")
			.storeAddress("address")
			.storeIntro("intro")
			.storePhone("02-2222-2222")
			.openTime(LocalTime.parse("00:01"))
			.closeTime(LocalTime.parse("23:59"))
			.deliveryTip(10)
			.foodCategory("중식")
			.minDeliveryPrice(2000)
			.build());

		Menu menu = menuRepository.save(new Menu(store,
			new MenuRequestDto("메인", "image.jpg", "짜장면", 5000, "맛있어요")));

		// ✅ when
		Order order = orderRepository.save(Order.builder()
			.user(user).store(store).menu(menu).build());

		// ✅ then
		assertThat(order.getTotalPrice()).isEqualTo(menu.getPrice() + store.getDeliveryTip());
	}

	@Test
	@DisplayName("주문 상태 변경 메서드가 제대로 작동하는지 확인합니다.")
	void changeOrderStatus() {
		// ✅ given
		User user = userRepository.save(User.builder()
			.email("1234@naver.com")
			.isDeleted(false)
			.nickname("user123")
			.password("dkdk1234!")
			.role(UserRole.USER)
			.address("address")
			.build());

		Store store = storeRepository.save(Store.builder()
			.storeName("중국집")
			.storeAddress("address")
			.storeIntro("intro")
			.storePhone("02-2222-2222")
			.openTime(LocalTime.parse("00:01"))
			.closeTime(LocalTime.parse("23:59"))
			.deliveryTip(10)
			.foodCategory("중식")
			.minDeliveryPrice(2000)
			.build());
		Menu menu = menuRepository.save(new Menu(store,
			new MenuRequestDto("메인", "image.jpg", "짜장면", 5000, "맛있어요")));
		Order order = orderRepository.save(Order.builder()
			.user(user).store(store).menu(menu).build());

		// ✅ when
		order.setOrderStatus(OrderStatus.ACCEPTED);

		// ✅ then
		assertThat(order.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
	}

	@DisplayName("주문 생성시에 초기 상태는 요청상태 입니다.")
	@Test
	void createOrderStatus() {
		// ✅ given
		User user = userRepository.save(User.builder()
			.email("1234@naver.com")
			.isDeleted(false)
			.nickname("user123")
			.password("dkdk1234!")
			.role(UserRole.USER)
			.address("address")
			.build());

		Store store = storeRepository.save(Store.builder()
			.storeName("중국집")
			.storeAddress("address")
			.storeIntro("intro")
			.storePhone("02-2222-2222")
			.openTime(LocalTime.parse("00:01"))
			.closeTime(LocalTime.parse("23:59"))
			.deliveryTip(10)
			.foodCategory("중식")
			.minDeliveryPrice(2000)
			.build());
		Menu menu = menuRepository.save(new Menu(store,
			new MenuRequestDto("메인", "image.jpg", "짜장면", 5000, "맛있어요")));

		// ✅ when
		Order order = orderRepository.save(Order.builder()
			.user(user).store(store).menu(menu).build());

		// ✅ then
		assertThat(order.getStatus()).isEqualByComparingTo(OrderStatus.REQUESTED);
	}
}