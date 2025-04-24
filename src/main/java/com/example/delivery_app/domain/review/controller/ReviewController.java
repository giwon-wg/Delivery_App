package com.example.delivery_app.domain.review.controller;

import com.example.delivery_app.domain.review.dto.ReviewDto;
import com.example.delivery_app.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
public class ReviewController {

    public final ReviewService reviewService;

    @PostMapping("/api/store/{storeId}/reviews")
    public void reviewsave(@RequestBody ReviewDto reviewDto){

        reviewService.reviewsave(reviewDto);
    }

}
