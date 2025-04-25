package com.example.delivery_app.domain.menu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.delivery_app.common.exception.CustomException;
import com.example.delivery_app.domain.menu.entity.Menu;
import com.example.delivery_app.domain.menu.exception.ErrorCode;

public interface MenuRepository extends JpaRepository<Menu, Long> {

	/**
	 * store에 해당하는 메뉴 리스트 출력
	 * @param storeStoreId
	 * @param status
	 * @return
	 */
	List<Menu> findAllByStore_StoreIdAndIsDeleted(Long storeStoreId, boolean status);

	List<Menu> findAllByStore_StoreId(Long storeId);

	default Menu findByIdOrElseThrow(Long menuId) {
		return findById(menuId).orElseThrow(() -> new CustomException(ErrorCode.MENU_NOT_FOUND));
	}

	/**
	 * 일부 단어만 검색했을 경우 그 단어를 포함한 메뉴 리스트를 출력
	 * @param storeStoreId
	 * @param menuName
	 * @param status
	 * @return
	 */
	List<Menu> findAllByStore_StoreIdAndMenuNameContainingAndIsDeleted(Long storeStoreId, String menuName,
		boolean status);

}
