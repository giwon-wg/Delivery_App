package com.example.delivery_app.domain.menu.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.delivery_app.domain.menu.entity.Menu;
import com.example.delivery_app.domain.menu.repository.MenuRepository;
import com.example.delivery_app.domain.store.entity.Store;
import com.example.delivery_app.domain.store.repository.StoreRepository;
import com.example.delivery_app.domain.user.entity.User;
import com.example.delivery_app.domain.user.entity.UserRole;
import com.example.delivery_app.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class MenuServiceImplTest {

	@Mock
	private MenuRepository menuRepository;

	@Mock
	private StoreRepository storeRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private MenuService menuService;

	private Store mockStore;
	private User mockUser;
	private Menu mockMenu;

	// 테스트 메서드마다 한 번씩 실행됨
	// 즉, @Test 메서드 하나 실행 시 마다 setUp 메서드가 한 번씩 호출됨
	// setUp 시에는 User도 필요하다 -> 권한 부여하고 id?
	// create로 postman 처음 실행할 때 하는 순서를 생각해보면 된다
	//  회원 가입 -> 로그인(단, 토큰은 필요없음) -> 가게 생성 -> 메뉴 생성
	// 근데 이렇게 하려니까 FixtureFactory라는 클래스를 만들어서 써야함 그래서 포기
	// @BeforeEach
	// void setUp() {
	// 	Map<String, Object> fieldValues = new HashMap<>();
	// 	fieldValues.put("id", 100L);
	// 	fieldValues.put("email", "test1@test.com");
	// 	fieldValues.put("password", "test1test1");
	// 	fieldValues.put("nickname", "testName");
	// 	fieldValues.put("role", Enum.valueOf(UserRole.class, "USER"));
	// 	fieldValues.put("address", "testAddress");
	// 	fieldValues.put("isDeleted", false);
	//
	//
	// }

	@BeforeEach
	void setUp() {
		// mock은 가짜 객체를 만드는 것
		// 즉, 아래와 같이 코드를 짜면 mockito 같은 테스트용 라이브러리가 대신 가짜 객체를 만들어준다
		// 이 객체들은 실제 로직을 타지 않기 때문에 DB에 저장되거나 하지는 않는다
		// 그리고 이 객체의 필드들은 메모리에는 존재하지 않기 때문에 getter로 값들을 불러올 때 기본값이 출력된다
		// 그렇기 때문에 값들을 넣어주기 위해 when().thenReturn()을 사용하는 것이다
		// 이렇게 하면 모든 세팅들을 안해주고 필요한 것들만 세팅해줄 수 있다
		mockUser = mock(User.class);
		mockStore = mock(Store.class);
		mockMenu = mock(Menu.class);

		// mockUser 관련
		when(mockUser.getId()).thenReturn(1L);
		when(mockUser.getEmail()).thenReturn("test1@test.com");
		when(mockUser.getPassword()).thenReturn("test1test1");
		when(mockUser.getRole()).thenReturn(Enum.valueOf(UserRole.class, "OWNER"));
		when(mockUser.isDeleted()).thenReturn(false);

		// mockStore 관련
		when(mockStore.getStoreId()).thenReturn(1L);
		when(mockStore.getUser()).thenReturn(mockUser);

		// mockMenu 관련
		when(mockMenu.getId()).thenReturn(1L);
		when(mockMenu.getStore()).thenReturn(mockStore);
		when(mockMenu.getCategory()).thenReturn("testCategory");
		when(mockMenu.getMenuName()).thenReturn("testMenuName");
		when(mockMenu.getPrice()).thenReturn(1000);
		when(mockMenu.getMenuContent()).thenReturn("testContent");
	}

	@DisplayName("메뉴를 저장합니다.")
	@Test
	void saveMenu() {
		// given - 준비
		// 미리 세팅해놓은 setUp() 함수를 통해 store, user를 생성
		setUp();

		// when - 실행
		Menu savedMenu = menuRepository.save(mockMenu);

		// then - 비교
		Optional<Menu> findMenu = menuRepository.findById(savedMenu.getId());
		assertThat(findMenu).isNotNull();
		assertThat(savedMenu.equals(findMenu));
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