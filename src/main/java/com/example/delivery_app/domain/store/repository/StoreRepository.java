package com.example.delivery_app.domain.store.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.delivery_app.domain.store.entity.Store;
import com.example.delivery_app.domain.store.enums.StoreStatus;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
	@Query("SELECT s FROM Store s WHERE s.status = :status")
	Page<Store> findAllByStatus(@Param("status") StoreStatus status, Pageable pageable);

	@Query("SELECT s FROM Store s LEFT JOIN FETCH s.menus WHERE s.status = :status AND s.storeId = :storeId")
	Optional<Store> findByIdAndStatusWithMenus(@Param("storeId") Long storeId, @Param("status") StoreStatus status);
}
