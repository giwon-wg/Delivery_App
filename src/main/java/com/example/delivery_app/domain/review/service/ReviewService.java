package com.example.delivery_app.domain.review.service;

import com.example.delivery_app.domain.order.service.OrderService;
import com.example.delivery_app.domain.review.dto.ReviewDto;
import com.example.delivery_app.domain.review.entity.Review;
import com.example.delivery_app.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReviewService {

    public final ReviewRepository reviewRepository;

    public Review reviewsave(ReviewDto reviewDto){
        //ReviewRepository reviewRepository =new ReviewRepository();

        Review review = new Review(
                1L,
                reviewDto.getRating(),
                reviewDto.getContent(),
                reviewDto.isStatus()
        );

        return reviewRepository.save(review);

    }

    public /*접근제어자*/ ReviewDto/**/ reviewfind(ReviewDto reviewDto){
        //RequiredArgsConstructor를 사용하여 new 생성자가 필요없으며 final로 처리하여 바꿀수 없음
        //ReviewRepository reviewRepository = new ReviewRepository();

        Review review = reviewRepository.findById().get();

        ReviewDto reviewDto2 = new ReviewDto(
                review.getRating(),
                review.getContent(),
                review.isStatus()
        );

        return reviewDto;
    }

}
