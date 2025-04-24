package com.example.delivery_app.domain.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.delivery_app.common.exception.CustomException;
import com.example.delivery_app.domain.order.entity.Order;
import com.example.delivery_app.domain.order.exception.OrderErrorCode;
import com.example.delivery_app.domain.user.entity.UserRole;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	@Query("SELECT o FROM orders o WHERE o.user.id = :userId AND o.user.role IN (:roles)")
	List<Order> findAllByUserIdAndRole(@Param("userId") Long userId, @Param("roles") List<UserRole> roles);

	default Order findByIdOrElseThrow(Long id) {
		return findById(id)
			.orElseThrow(() -> new CustomException(OrderErrorCode.ORDER_NOT_FOUND));
	}
}
