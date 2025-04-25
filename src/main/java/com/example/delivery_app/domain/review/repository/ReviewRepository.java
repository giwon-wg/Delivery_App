package com.example.delivery_app.domain.review.repository;

import com.example.delivery_app.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
