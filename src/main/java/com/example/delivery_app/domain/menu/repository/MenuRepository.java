package com.example.delivery_app.domain.menu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.delivery_app.domain.menu.entity.Menu;
import com.example.delivery_app.domain.menu.exception.CustomException;
import com.example.delivery_app.domain.menu.exception.ErrorCode;

import io.lettuce.core.dynamic.annotation.Param;

public interface MenuRepository extends JpaRepository<Menu, Long> {

	@Query("SELECT m FROM Menu m WHERE m.store.storeId = :storeId AND m.status = :status")
	List<Menu> findByIdAndStatus(@Param("storeId") Long storeId, @Param("status") boolean status);

	default List<Menu> findByIdOrElseThrow(Long storeId, boolean status) {
		if (storeId == null) {
			throw new CustomException(ErrorCode.MENU_NOT_FOUND);
		}
		return findByIdAndStatus(storeId, status);
	}

	/**
	 * JPQL 확인을 위한 임시 메서드
	 * @return
	 */
	@Query("SELECT m FROM Menu m WHERE m.status = :status")
	List<Menu> findAll(@Param("status") boolean status);
}
