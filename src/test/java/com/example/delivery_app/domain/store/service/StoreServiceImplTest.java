package com.example.delivery_app.domain.store.service;

import com.example.delivery_app.common.exception.CustomException;
import com.example.delivery_app.domain.store.dto.request.StoreOperatingTimeRequestDto;
import com.example.delivery_app.domain.store.dto.request.StoreRequestDto;
import com.example.delivery_app.domain.store.dto.response.StoreDeleteResponseDto;
import com.example.delivery_app.domain.store.dto.response.StoreResponseDto;
import com.example.delivery_app.domain.store.entity.Store;
import com.example.delivery_app.domain.store.enums.StoreStatus;
import com.example.delivery_app.domain.store.exception.StoreErrorCode;
import com.example.delivery_app.domain.store.repository.StoreRepository;
import com.example.delivery_app.domain.user.Auth.UserAuth;
import com.example.delivery_app.domain.user.entity.User;
import com.example.delivery_app.domain.user.entity.UserRole;
import com.example.delivery_app.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;


class StoreServiceImplTest {
    @Mock
    private StoreRepository storeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private StoreServiceImpl storeService;

    private User user;
    private Store store;
    private UserAuth userAuth;

    @BeforeEach
    void SetUp(){
        MockitoAnnotations.openMocks(this);


        user = User.builder()
                .email("test@deliveryhajo.com")
                .nickname("nickname")
                .address("address")
                .role(UserRole.OWNER)
                .isDeleted(false)
                .build();
        ReflectionTestUtils.setField(user, "id", 1L);

        store = Store.builder()
                .storeName("Test Store")
                .storeAddress("Test Address")
                .foodCategory("한식")
                .storePhone("010-1234-5678")
                .minDeliveryPrice(10000)
                .deliveryTip(2000)
                .openTime(LocalTime.of(9, 0))
                .closeTime(LocalTime.of(22, 0))
                .user(user)
                .build();
        ReflectionTestUtils.setField(store, "storeId", 1L);

        userAuth = UserAuth.of(user.getId(), List.of(UserRole.OWNER));
    }

    // 가게 등록

    @Test
    @DisplayName("가게 등록 성공")
    void 가게등록성공(){

        //given
        StoreRequestDto request = new StoreRequestDto(
                "Test Store","TestAddress","한식","010-1234-5678","맛집입니다",
                10000,2000,LocalTime.of(9,0),LocalTime.of(22,0)
        );

        given(storeRepository.countActiveStoresByUserId(anyLong())).willReturn(0L);
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(storeRepository.save(any(Store.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When
        StoreResponseDto response = storeService.saveStore(request, userAuth);

        // Then
        assertThat(response.getStoreName()).isEqualTo("Test Store");
    }


    @Test
    @DisplayName("가게 등록 실패 - 가게 개수 초과")
    void 가게등록실패_가게_개수_초과() {
        // Given
        StoreRequestDto request = new StoreRequestDto(
                "Test Store", "Test Address", "한식", "010-1234-5678", "맛집입니다",
                10000, 2000, LocalTime.of(9, 0), LocalTime.of(22, 0)
        );
        given(storeRepository.countActiveStoresByUserId(anyLong())).willReturn(3L);

        // When
        Throwable thrown = catchThrowable(() -> storeService.saveStore(request, userAuth));

        // Then
        assertThat(thrown).isInstanceOf(CustomException.class)
                .satisfies(e -> assertThat(((CustomException) e).getResponseCode())
                        .isEqualTo(StoreErrorCode.STORE_LIMIT_EXCEEDED));
    }

    @Test
    @DisplayName("가게 등록 실패 - 유저 없음")
    void 가게등록실패_유저없음() {
        // Given
        StoreRequestDto request = new StoreRequestDto(
                "Test Store", "Test Address", "한식", "010-1234-5678", "맛집입니다",
                10000, 2000, LocalTime.of(9, 0), LocalTime.of(22, 0)
        );
        given(storeRepository.countActiveStoresByUserId(anyLong())).willReturn(0L);
        given(userRepository.findById(anyLong())).willReturn(Optional.empty()); // 유저 없음

        // When
        Throwable thrown = catchThrowable(() -> storeService.saveStore(request, userAuth));

        // Then
        assertThat(thrown)
                .isInstanceOf(CustomException.class)
                .satisfies(e -> assertThat(((CustomException) e).getResponseCode())
                        .isEqualTo(StoreErrorCode.USER_NOT_FOUND));
    }


    //  가게 단건 조회

    @Test
    @DisplayName("가게 조회 성공")
    void 가게조회성공() {
        // Given
        given(storeRepository.findByIdAndStatusWithMenus(anyLong(), eq(StoreStatus.ACTIVE)))
                .willReturn(Optional.of(store));

        // When
        StoreResponseDto response = storeService.getStoreById(1L);

        // Then
        assertThat(response.getStoreName()).isEqualTo("Test Store");
    }

    @Test
    @DisplayName("가게 조회 실패 - 가게가 없음")
    void 가게조회실패_가게_없음() {
        // Given
        given(storeRepository.findByIdAndStatusWithMenus(anyLong(), eq(StoreStatus.ACTIVE)))
                .willReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(() -> storeService.getStoreById(1L));

        // Then
        assertThat(thrown).isInstanceOf(CustomException.class)
                .satisfies(e -> assertThat(((CustomException) e).getResponseCode())
                        .isEqualTo(StoreErrorCode.STORE_NOT_FOUND));
    }

    // --- 가게 수정 ---

    @Test
    @DisplayName("가게 수정 성공")
    void 가게수정성공() {
        // Given
        StoreRequestDto updateRequest = new StoreRequestDto(
                "New Store", "New Address", "양식", "010-0000-0000", "새로운 소개글",
                15000, 2500, LocalTime.of(10, 0), LocalTime.of(21, 0)
        );
        given(storeRepository.findByIdAndStatusWithMenus(anyLong(), eq(StoreStatus.ACTIVE)))
                .willReturn(Optional.of(store));

        // When
        StoreResponseDto response = storeService.updateStore(1L, updateRequest, userAuth);

        // Then
        assertThat(response.getStoreName()).isEqualTo("New Store");
        assertThat(response.getStoreAddress()).isEqualTo("New Address");
    }

    @Test
    @DisplayName("가게 수정 실패 - 소유주 아님")
    void 가게수정실패_소유주아님() {
        // Given
        UserAuth otherUser = UserAuth.of(999L, List.of(UserRole.USER));
        StoreRequestDto updateRequest = new StoreRequestDto(
                "New Store", "New Address", "양식", "010-0000-0000", "새로운 소개글",
                15000, 2500, LocalTime.of(10, 0), LocalTime.of(21, 0)
        );
        given(storeRepository.findByIdAndStatusWithMenus(anyLong(), eq(StoreStatus.ACTIVE)))
                .willReturn(Optional.of(store));

        // When
        Throwable thrown = catchThrowable(() -> storeService.updateStore(1L, updateRequest, otherUser));

        // Then
        assertThat(thrown).isInstanceOf(CustomException.class)
                .satisfies(e -> assertThat(((CustomException) e).getResponseCode())
                        .isEqualTo(StoreErrorCode.NOT_STORE_OWNER));
    }

    @Test
    @DisplayName("가게 수정 실패 - 가게가 없음")
    void 가게수정실패_가게_없음() {
        // Given
        StoreRequestDto updateRequest = new StoreRequestDto(
                "New Store", "New Address", "양식", "010-0000-0000", "새로운 소개글",
                15000, 2500, LocalTime.of(10, 0), LocalTime.of(21, 0)
        );
        given(storeRepository.findByIdAndStatusWithMenus(anyLong(), eq(StoreStatus.ACTIVE)))
                .willReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(() -> storeService.updateStore(1L, updateRequest, userAuth));

        // Then
        assertThat(thrown).isInstanceOf(CustomException.class)
                .satisfies(e -> assertThat(((CustomException) e).getResponseCode())
                        .isEqualTo(StoreErrorCode.STORE_NOT_FOUND));
    }

    // --- 가게 삭제 ---

    @Test
    @DisplayName("가게 삭제 성공")
    void 가게삭제성공() {
        // Given
        given(storeRepository.findByIdAndStatusWithMenus(anyLong(), eq(StoreStatus.ACTIVE)))
                .willReturn(Optional.of(store));

        // When
        StoreDeleteResponseDto response = storeService.deleteStore(1L, userAuth);

        // Then
        assertThat(response.getStoreId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("가게 삭제 실패 - 소유주 아님")
    void 가게삭제실패_소유주아님() {
        // Given
        UserAuth otherUser = UserAuth.of(999L, List.of(UserRole.USER));
        given(storeRepository.findByIdAndStatusWithMenus(anyLong(), eq(StoreStatus.ACTIVE)))
                .willReturn(Optional.of(store));

        // When
        Throwable thrown = catchThrowable(() -> storeService.deleteStore(1L, otherUser));

        // Then
        assertThat(thrown).isInstanceOf(CustomException.class)
                .satisfies(e -> assertThat(((CustomException) e).getResponseCode())
                        .isEqualTo(StoreErrorCode.NOT_STORE_OWNER));
    }

    @Test
    @DisplayName("가게 삭제 실패 - 가게가 없음")
    void 가게삭제실패_가게_없음() {
        // Given
        given(storeRepository.findByIdAndStatusWithMenus(anyLong(), eq(StoreStatus.ACTIVE)))
                .willReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(() -> storeService.deleteStore(1L, userAuth));

        // Then
        assertThat(thrown).isInstanceOf(CustomException.class)
                .satisfies(e -> assertThat(((CustomException) e).getResponseCode())
                        .isEqualTo(StoreErrorCode.STORE_NOT_FOUND));
    }

    // --- 가게 영업시간 수정 ---

    @Test
    @DisplayName("영업 시간 수정 성공")
    void 영업시간수정성공() {
        // Given
        StoreOperatingTimeRequestDto request = new StoreOperatingTimeRequestDto(
                LocalTime.of(8, 0), LocalTime.of(20, 0)
        );
        given(storeRepository.findByIdAndStatusWithMenus(anyLong(), eq(StoreStatus.ACTIVE)))
                .willReturn(Optional.of(store));

        // When
        StoreResponseDto response = storeService.updateOperatingTime(1L, request, userAuth);

        // Then
        assertThat(response.getOpenTime()).isEqualTo("08:00");
        assertThat(response.getCloseTime()).isEqualTo("20:00");
    }

    @Test
    @DisplayName("영업 시간 수정 실패 - 소유주 아님")
    void 영업시간수정실패_소유주아님() {
        // Given
        UserAuth otherUser = UserAuth.of(999L, List.of(UserRole.USER));
        StoreOperatingTimeRequestDto request = new StoreOperatingTimeRequestDto(
                LocalTime.of(8, 0), LocalTime.of(20, 0)
        );
        given(storeRepository.findByIdAndStatusWithMenus(anyLong(), eq(StoreStatus.ACTIVE)))
                .willReturn(Optional.of(store));

        // When
        Throwable thrown = catchThrowable(() -> storeService.updateOperatingTime(1L, request, otherUser));

        // Then
        assertThat(thrown).isInstanceOf(CustomException.class)
                .satisfies(e -> assertThat(((CustomException) e).getResponseCode())
                        .isEqualTo(StoreErrorCode.NOT_STORE_OWNER));
    }

    @Test
    @DisplayName("영업 시간 수정 실패 - 가게 없음")
    void 영업시간수정실패_가게_없음() {
        // Given
        StoreOperatingTimeRequestDto request = new StoreOperatingTimeRequestDto(
                LocalTime.of(8, 0), LocalTime.of(20, 0)
        );
        given(storeRepository.findByIdAndStatusWithMenus(anyLong(), eq(StoreStatus.ACTIVE)))
                .willReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(() -> storeService.updateOperatingTime(1L, request, userAuth));

        // Then
        assertThat(thrown).isInstanceOf(CustomException.class)
                .satisfies(e -> assertThat(((CustomException) e).getResponseCode())
                        .isEqualTo(StoreErrorCode.STORE_NOT_FOUND));
    }

    // --- 전체 가게 리스트 조회 ---

    @Test
    @DisplayName("전체 가게 리스트 조회 성공")
    void 전체가게리스트조회성공() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        given(storeRepository.findAllByStatus(eq(StoreStatus.ACTIVE), eq(pageable)))
                .willReturn(new PageImpl<>(List.of(store)));

        // When
        var result = storeService.getAllStoreList(pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getStoreName()).isEqualTo("Test Store");
    }
}


