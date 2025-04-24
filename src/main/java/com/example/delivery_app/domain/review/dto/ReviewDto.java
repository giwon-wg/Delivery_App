package com.example.delivery_app.domain.review.dto;

import lombok.Getter;

@Getter
public class ReviewDto {

    int rating;

    String content;

    boolean status;

    public ReviewDto (int rating, String content, boolean status){
        this.rating = rating;
        this.content = content;
        this.status = status;
    }
}
