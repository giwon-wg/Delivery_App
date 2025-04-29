package com.example.delivery_app.domain.review.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReviewRequestDto {

    private final int rating;

    private final String content;

    private final boolean status;

}
