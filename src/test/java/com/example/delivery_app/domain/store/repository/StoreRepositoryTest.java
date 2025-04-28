package com.example.delivery_app.domain.store.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;
import java.util.List;

import com.example.delivery_app.common.exception.CustomException;
import com.example.delivery_app.domain.store.entity.Store;
import com.example.delivery_app.domain.store.enums.StoreStatus;
import com.example.delivery_app.domain.user.entity.User;
import com.example.delivery_app.domain.user.entity.UserRole;
import com.example.delivery_app.domain.user.repository.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
@Transactional
class StoreRepositoryTest {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    private User user;
    private Store store;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test@test.com")
                .password("password")
                .nickname("nickname")
                .role(UserRole.OWNER)
                .address("address")
                .isDeleted(false)
                .build();
        userRepository.save(user);

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
        storeRepository.save(store);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("findAllByStatus 성공 - ACTIVE 가게 조회")
    void findAllByStatus_성공() {
        // Given
        PageRequest pageRequest = PageRequest.of(0, 10);

        // When
        Page<Store> stores = storeRepository.findAllByStatus(StoreStatus.ACTIVE, pageRequest);

        // Then
        assertThat(stores.getContent()).hasSize(1);
        assertThat(stores.getContent().get(0).getStoreName()).isEqualTo("Test Store");
    }

    @Test
    @DisplayName("findByIdAndStatusWithMenus 성공 - ACTIVE 가게와 메뉴 조회")
    void findByIdAndStatusWithMenus_성공() {
        // When
        Store foundStore = storeRepository.findByIdAndStatusWithMenus(store.getStoreId(), StoreStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Store not found"));

        // Then
        assertThat(foundStore.getStoreName()).isEqualTo("Test Store");
        assertThat(foundStore.getMenus()).isEmpty(); // 지금은 메뉴가 없으니까
    }

    @Test
    @DisplayName("countActiveStoresByUserId 성공 - 유저의 ACTIVE 가게 수 조회")
    void countActiveStoresByUserId_성공() {
        // When
        long count = storeRepository.countActiveStoresByUserId(user.getId());

        // Then
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("findByIdOrElseThrow 성공 - 존재하는 가게 조회")
    void findByIdOrElseThrow_성공() {
        // When
        Store foundStore = storeRepository.findByIdOrElseThrow(store.getStoreId());

        // Then
        assertThat(foundStore.getStoreName()).isEqualTo("Test Store");
    }

    @Test
    @DisplayName("findByIdOrElseThrow 실패 - 존재하지 않는 가게 조회")
    void findByIdOrElseThrow_실패() {
        // When
        Throwable thrown = catchThrowable(() -> storeRepository.findByIdOrElseThrow(999L));

        // Then
        assertThat(thrown)
                .isInstanceOf(CustomException.class)
                .satisfies(e -> {
                    CustomException ce = (CustomException) e;
                    assertThat(ce.getResponseCode()).isEqualTo(com.example.delivery_app.domain.store.exception.StoreErrorCode.STORE_NOT_FOUND);
                });
    }
}