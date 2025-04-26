package com.example.delivery_app.domain.menu.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.delivery_app.domain.menu.repository.MenuRepository;
import com.example.delivery_app.domain.store.entity.Store;
import com.example.delivery_app.domain.store.repository.StoreRepository;

@ExtendWith(MockitoExtension.class)
class MenuServiceImplTest {

	@Mock
	private MenuRepository menuRepository;

	@Mock
	private StoreRepository storeRepository;

	@InjectMocks
	private MenuService menuService;

	private Long storeId;
	private Long menuId;
	private Store store;

	// 테스트 메서드마다 한 번씩 실행됨
	// 즉, @Test 메서드 하나 실행 시 마다 setUp 메서드가 한 번씩 호출됨
	// setUp 시에는 User도 필요하다 -> 권한 부여하고 id?
	// create로 postman 처음 실행할 때 하는 순서를 생각해보면 된다
	//  회원 가입 -> 로그인(단, 토큰은 필요없음) -> 가게 생성 -> 메뉴 생성
	@BeforeEach
	void setUp() {
		Map<String, Object> fieldValues = new HashMap<>();
		fieldValues.put("id", 1L);
		fieldValues.put("email", "test1@test.com");
		fieldValues.put("password", "test1test1");
		fieldValues.put("nicname", "testName");
		fieldValues.put("role", List.of("USER"));
	}

	@Test
	void saveMenu() {
		// given - 준비

		// when - 실행

		// then - 비교
	}

	@Test
	void updateMenu() {
	}

	@Test
	void deleteMenu() {
	}

	@Test
	void findMenu() {
	}
}