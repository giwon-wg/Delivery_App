package com.example.delivery_app.domain.review.service;

import com.example.delivery_app.domain.review.dto.ReviewDto;
import com.example.delivery_app.domain.review.entity.Review;
import com.example.delivery_app.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReviewService {

    public final ReviewRepository reviewRepository;

    public void reviewsave(ReviewDto reviewDto){
        //dto > entity로 변경 >>repository는 api사용으로 entity가 필요함
        Review review = new Review(
                1L,
                reviewDto.getRating(),
                reviewDto.getContent(),
                reviewDto.isStatus()
        );

        reviewRepository.save(review);
    }
}
