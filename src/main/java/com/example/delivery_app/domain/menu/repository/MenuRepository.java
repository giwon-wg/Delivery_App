package com.example.delivery_app.domain.menu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.delivery_app.domain.menu.entity.Menu;

import io.lettuce.core.dynamic.annotation.Param;

public interface MenuRepository extends JpaRepository<Menu, Long> {

	/**
	 * store에 해당하는 메뉴 리스트 출력
	 * @param storeId
	 * @param status
	 * @return
	 */
	@Query("SELECT m FROM Menu m WHERE m.store.storeId = :storeId AND m.status = :status")
	List<Menu> findByIdAndStatus(@Param("storeId") Long storeId, @Param("status") boolean status);

	default List<Menu> findByIdOrElseThrow(Long storeId, boolean status) {
		return findByIdAndStatus(storeId, status);
	}

	/**
	 * JPQL 확인을 위한 임시 메서드
	 * @return
	 */
	@Query("SELECT m FROM Menu m WHERE m.status = :status")
	List<Menu> findAll(@Param("status") boolean status);

	/**
	 * 일부 단어만 검색했을 경우 그 단어를 포함한 메뉴 리스트를 출력
	 * @param storeId
	 * @param word
	 * @param status
	 * @return
	 */
	@Query("SELECT m FROM Menu m WHERE m.store.storeId = :storeId AND m.menuName LIKE CONCAT('%', :word, '%') AND m.status = :status")
	List<Menu> findAllByIdByWord(@Param("storeId") Long storeId, @Param("word") String word, boolean status);

	// List<Menu> findAllBy
	// 쿼리 메서드로 AND 연산자를 활용하여 하는 법
	// LIKE -> Startinglike 할건지 Endinglike 할건지
	// containing은 어찌할건지 <- 쿼리메서드에서 활용하기 위해 만든 단어
	// 쿼리 메서드는 join말구 단일 엔티티 조회시 매우 유용
}
