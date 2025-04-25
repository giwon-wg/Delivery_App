package com.example.delivery_app.domain.review.service;

import com.example.delivery_app.domain.review.dto.ReviewRequestDto;
import com.example.delivery_app.domain.review.entity.Review;
import com.example.delivery_app.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReviewService {

    public final ReviewRepository reviewRepository;

    public Review reviewsave(ReviewRequestDto reviewDto, Long storeId){
        //ReviewRepository reviewRepository =new ReviewRepository();

        Review review = new Review(
                1L,
                reviewDto.getRating(),
                reviewDto.getContent(),
                reviewDto.isStatus()
        );

        return reviewRepository.save(review);

    }


    public /*접근제어자*/ ReviewRequestDto/*리턴값*/ reviewfind/*이름*/(Long storeId, Long reviewId/*매개변수*/){
        //RequiredArgsConstructor를 사용하여 new 생성자가 필요없으며 final로 처리하여 바꿀수 없음
        //ReviewRepository reviewRepository = new ReviewRepository();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(()-> new RuntimeException("리뷰를 찾을수 없습니다."));

        ReviewRequestDto reviewDto = new ReviewRequestDto(
                review.getRating(),
                review.getContent(),
                review.isStatus()
        );

        return reviewDto;
    }

}
