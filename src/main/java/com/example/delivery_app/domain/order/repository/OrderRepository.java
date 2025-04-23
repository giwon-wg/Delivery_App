package com.example.delivery_app.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.delivery_app.domain.order.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}
