package com.example.delivery_app.domain.review.controller;

import com.example.delivery_app.domain.review.dto.ReviewDto;
import com.example.delivery_app.domain.review.entity.Review;
import com.example.delivery_app.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/api/store/{storeId}/reviews")
    public Review reviewsave(@RequestBody ReviewDto reviewDto){

        return reviewService.reviewsave(reviewDto);
    }

    @GetMapping("/api/store/{storeId}/reviews")
    public ReviewDto reviewfind(@RequestBody ReviewDto reviewDto){

        return reviewService.reviewfind(reviewDto);
    }
}
