package com.example.delivery_app.domain.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.delivery_app.domain.store.entity.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

}
