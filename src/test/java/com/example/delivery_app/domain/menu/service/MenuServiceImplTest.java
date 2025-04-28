package com.example.delivery_app.domain.menu.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.delivery_app.domain.menu.dto.requestdto.MenuRequestDto;
import com.example.delivery_app.domain.menu.dto.requestdto.UpdateMenuRequestDto;
import com.example.delivery_app.domain.menu.dto.responsedto.MenuCreateResponseDto;
import com.example.delivery_app.domain.menu.dto.responsedto.MenuResponseDto;
import com.example.delivery_app.domain.menu.dto.responsedto.UpdateMenuResponseDto;
import com.example.delivery_app.domain.menu.entity.Menu;
import com.example.delivery_app.domain.menu.repository.MenuRepository;
import com.example.delivery_app.domain.store.entity.Store;
import com.example.delivery_app.domain.store.repository.StoreRepository;
import com.example.delivery_app.domain.user.entity.User;
import com.example.delivery_app.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
	// @MockitoSettings(strictness = LENIENT)
class MenuServiceImplTest {

	@Mock
	private MenuRepository menuRepository;

	@Mock
	private StoreRepository storeRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private MenuServiceImpl menuServiceImpl;

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

		// 이렇게 설정하는 걸 stubbing(약속)이라고 한다.
		// 근데 이것들 사실 다 필요없는 설정들이었다. 실제 객체를 만들어서 테스트 해야하기 때문에 아마 여기에 실제 메뉴들을 생성해놓는게 맞았던 것 같다
		// mockUser 관련
		// when(mockUser.getId()).thenReturn(1L);
		// when(mockUser.getEmail()).thenReturn("test1@test.com");
		// when(mockUser.getPassword()).thenReturn("test1test1");
		// when(mockUser.getRole()).thenReturn(Enum.valueOf(UserRole.class, "OWNER"));
		// when(mockUser.isDeleted()).thenReturn(false);
		//
		// // mockStore 관련
		// when(mockStore.getStoreId()).thenReturn(1L);
		// when(mockStore.getUser()).thenReturn(mockUser);
		//
		// mockMenu 관련
		// when(mockMenu.getId()).thenReturn(1L);
		// when(mockMenu.getStore()).thenReturn(mockStore);
		// when(mockMenu.getCategory()).thenReturn("testCategory");
		// when(mockMenu.getMenuName()).thenReturn("testMenuName");
		// when(mockMenu.getPrice()).thenReturn(1000);
		// when(mockMenu.getMenuContent()).thenReturn("testContent");
		// when(mockMenu.isDeleted()).thenReturn(false);
	}

	@DisplayName("메뉴를 저장합니다.")
	@Test
	void saveMenu_test() {
		// given - 준비
		// 미리 세팅해놓은 setUp() 함수를 통해 store, user를 생성
		// 근데 여기서 쓸모없는 stubbing이 있다는 에러가 떴다
		// -> 클래스 레벨에 @MockitoSettings(strictness = LENIENT) 를 사용하여 쓸모없는 stubbing 있어도 테스트 코드를 진행할 수 있도록 하였다
		// 근데 다시 보니까 setUp은 mockMenu 등에 대한 것이므로 필요가 없을 것 같다..
		// setUp();

		Store store = new Store();
		ReflectionTestUtils.setField(store, "storeId", 1L);

		Menu menu = Menu.builder()
			.store(store)
			.category("testCategory")
			.menuPicture("")
			.menuName("testMenuName")
			.price(1000)
			.menuContent("testContent")
			.build();
		ReflectionTestUtils.setField(menu, "id", 1L);

		// MenuRequestDto 관련
		MenuRequestDto dto = new MenuRequestDto(
			"testCategory",
			"",
			"testMenuName",
			1000,
			"testContent");

		// 실제 MenuServiceImpl 에서 saveMenu 안에는 findStore 로직과 save 로직이 있다
		// 테스트 코드에서도 이것들을 실행할것인데 테스트 코드에선 실제 DB에 저장하는게 아니므로 여기에 그에 대한 조치를 해줘야한다
		when(storeRepository.findByIdOrElseThrow(anyLong())).thenReturn(store);
		when(menuRepository.save(any(Menu.class))).thenReturn(menu);

		// when - 실행
		MenuCreateResponseDto savedMenu = menuServiceImpl.saveMenu(1L, dto);

		// then - 비교
		// assertThat(savedMenu.equals(responseDto)를 하면 주소 값 비교가 되기 때문에 값들을 꺼내서 비교해야한다
		assertThat(savedMenu.getStoreId()).isEqualTo(1L);
		assertThat(savedMenu.getId()).isEqualTo(1L);
		assertThat(savedMenu.getCategory()).isEqualTo("testCategory");
		assertThat(savedMenu.getMenuPicture()).isEqualTo("");
		assertThat(savedMenu.getMenuName()).isEqualTo("testMenuName");
		assertThat(savedMenu.getPrice()).isEqualTo(1000);
		assertThat(savedMenu.getMenuContent()).isEqualTo("testContent");
	}

	@DisplayName("메뉴를 수정합니다.")
	@Test
	void updateMenu_test() {
		// given
		// mock 객체는 실제 로직을 타지 않는다
		// 따라서 실제 로직을 타기 위해선(when 부분을 하기 위해선) 실제 객체 생성이 필요하다
		Menu menu = Menu.builder()
			.menuName("testMenuName")
			.price(1000)
			.menuContent("testMenuContent")
			.build();

		ReflectionTestUtils.setField(menu, "id", 1L);

		UpdateMenuRequestDto dto = new UpdateMenuRequestDto(
			"",
			"testUpdateMenuName",
			1000,
			"testMenuContent"
		);

		// 여기서 anyLong()을 앞에 넣어줬으면 다른 것들도 모두 이런 형식으로 넣어줘야 한다
		// boolean의 경우 eq()가 그것!
		// false 만 입력하는 건 raw 값 이라고 한다
		// anyLong() 이런식으로 하는 건 matcher라고 한다
		// when(storeRepository.findByIdOrElseThrow(anyLong())).thenReturn(mockStore);
		when(menuRepository.findAllByStore_StoreIdAndIsDeleted(anyLong(), eq(false))).thenReturn(List.of(menu));

		// when
		UpdateMenuResponseDto updateMenu = menuServiceImpl.updateMenu(1L, 1L, dto);

		// then
		assertThat(updateMenu.getMenuPicture()).isEqualTo("");
		assertThat(updateMenu.getMenuName()).isEqualTo("testUpdateMenuName");
		assertThat(updateMenu.getPrice()).isEqualTo(1000);
		assertThat(updateMenu.getMenuContent()).isEqualTo("testMenuContent");

		// 근데 이렇게 하면 실제 객체 생성해서 임의의 값을 넣어주고 그냥 로직 돌려본 거 같은데
		// 이러면 mock 객체는 왜 필요하고 테스트 코드는 왜하는거지? 그냥 실제 로직에서 돌려보면 되는거 아닌가?
		// ->
		// 여기서 사용하는 repository는 mock 객체. 즉 실제 DB에 저장하거나 그런게 아니다
		// 즉, mock 객체는 외부 의존성(DB 접근 등)을 차단하는 용도
		// 테스트 데이터는 진짜 객체로 하는게 맞다!
		// 외부 의존성(DB, API 호출 등)은 mock 처리를 하고 내가 검증하고 싶은 로직(값 저장, 수정 등)은 진짜 객체로 테스트하는 것!
	}

	@Test
	void deleteMenu_test() {
		// given
		Store store = new Store();

		Menu menu = new Menu();
		ReflectionTestUtils.setField(menu, "id", 1L);
		ReflectionTestUtils.setField(menu, "isDeleted", false);

		when(storeRepository.findByIdOrElseThrow(anyLong())).thenReturn(store);
		when(menuRepository.findAllByStore_StoreIdAndIsDeleted(anyLong(), eq(false))).thenReturn(List.of(menu));

		// when
		menuServiceImpl.deleteMenu(1L, 1L);

		// then
		assertThat(menu.isDeleted()).isTrue();
	}

	@Test
	void findMenu_test() {
		// given
		Store store = new Store();
		ReflectionTestUtils.setField(store, "storeId", 1L);

		Menu menu1 = Menu.builder()
			.store(store)
			.category("testCategory1")
			.menuPicture("")
			.menuName("testMenuName1")
			.price(1000)
			.menuContent("testContent1")
			.build();
		ReflectionTestUtils.setField(menu1, "id", 1L);

		Menu menu2 = Menu.builder()
			.store(store)
			.category("testCategory2")
			.menuPicture("")
			.menuName("testMenuName2")
			.price(2000)
			.menuContent("testContent2")
			.build();
		ReflectionTestUtils.setField(menu2, "id", 2L);

		List<Menu> menus = new ArrayList<>();
		menus.add(menu1);
		menus.add(menu2);

		MenuResponseDto dto1 = MenuResponseDto.fromMenu(menu1);
		MenuResponseDto dto2 = MenuResponseDto.fromMenu(menu2);

		List<MenuResponseDto> dtos = new ArrayList<>();

		dtos.add(dto1);
		dtos.add(dto2);

		when(storeRepository.findByIdOrElseThrow(anyLong())).thenReturn(store);
		when(menuRepository.findAllByStore_StoreIdAndMenuNameContainingAndIsDeleted(anyLong(), anyString(),
			eq(false))).thenReturn(menus);

		// when
		List<MenuResponseDto> testList = menuServiceImpl.findMenu(1L, "test");

		// then
		assertThat(testList).containsAnyElementsOf(dtos);
	}
}