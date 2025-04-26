package com.example.delivery_app.domain.store.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.delivery_app.common.exception.CustomException;
import com.example.delivery_app.domain.store.entity.Store;
import com.example.delivery_app.domain.store.enums.StoreStatus;
import com.example.delivery_app.domain.store.exception.StoreErrorCode;

import io.lettuce.core.dynamic.annotation.Param;

/**
 * StoreRepository는 Store 엔티티와 관련된 DB 접근을 담당합니다.
 * JpaRepository를 상속하여 기본적인 CRUD 기능 외에, 상태(status) 기반의 커스텀 쿼리를 제공합니다.
 */
@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

	/**
	 * 주어진 상태값에 해당하는 가게 목록을 페이지네이션하여 조회합니다.
	 *
	 * @param status   조회할 가게의 상태 (예: ACTIVE)
	 * @param pageable 페이지 정보
	 * @return 상태에 해당하는 가게들의 Page 객체
	 */
	@Query("SELECT s FROM Store s WHERE s.status = :status")
	Page<Store> findAllByStatus(@Param("status") StoreStatus status, Pageable pageable);

	/**
	 * 특정 ID와 상태를 가진 가게를 조회하며, 연관된 메뉴 정보도 함께 가져옵니다.
	 *
	 * @param storeId 조회할 가게의 ID
	 * @param status  가게의 상태 (예: ACTIVE)
	 * @return 조건에 맞는 가게 정보 (Optional)
	 */
	@Query("SELECT s FROM Store s "
		+ "LEFT JOIN FETCH s.menus m "
		+ "WHERE s.status = :status AND s.storeId = :storeId")
	Optional<Store> findByIdAndStatusWithMenus(@Param("storeId") Long storeId, @Param("status") StoreStatus status);

	/**
	 * 특정 유저가 운영 중인(ACTIVE) 가게 수를 반환합니다.
	 *
	 * @param userId 유저 ID
	 * @return 해당 유저가 운영 중인 가게 수
	 */
	@Query("SELECT COUNT(s) FROM Store s WHERE s.user.id = :userId AND s.status = 'ACTIVE'")
	long countActiveStoresByUserId(@Param("userId") Long userId);

	/**
	 * ID로 가게를 조회하며, 존재하지 않을 경우 CustomException을 발생시킵니다.
	 *
	 * @param storeId 조회할 가게의 ID
	 * @return 존재하는 경우 Store 객체 반환
	 * @throws CustomException STORE_NOT_FOUND 예외
	 */
	default Store findByIdOrElseThrow(Long storeId) {
		return findById(storeId)
			.orElseThrow(() -> new CustomException(StoreErrorCode.STORE_NOT_FOUND));
	}
}
