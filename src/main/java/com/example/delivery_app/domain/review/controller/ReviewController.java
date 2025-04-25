package com.example.delivery_app.domain.review.controller;

import com.example.delivery_app.domain.review.dto.ReviewRequestDto;
import com.example.delivery_app.domain.review.entity.Review;
import com.example.delivery_app.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/store/{storeId}/reviews")
public class ReviewController {

    private ReviewService reviewService;

    @PostMapping
    public Review saveReview(
            @RequestBody ReviewRequestDto reviewRequestDto,
            @PathVariable(name = "storeId") Long storeId){

        return reviewService.reviewsave(reviewRequestDto, storeId);
    }

    @GetMapping("/{reviewId}")
    public ReviewRequestDto reviewfind(
            @PathVariable(name = "storeId") Long storeId,
            @PathVariable(name = "reviewId") Long reviewId
    ){

        return reviewService.reviewfind(storeId, reviewId);
    }
}
