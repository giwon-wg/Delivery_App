package com.example.delivery_app.domain.review.entity;

import com.example.delivery_app.domain.store.entity.Store;
import com.example.delivery_app.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter                 // @Id, 기본 생성자 필수
@Entity                 // 클래스를 jap entity로 만들어 준다.> table과 매핑해준다.
@Table(name = "review") // @entity > 지정 db의 이름
@NoArgsConstructor      // 매개변수 없이도 생성 가능
public class Review {

    @Id     //테이블의 pk값 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동으로 1씩 증가
    private Long id;

    private int rating;

    private String content;

    private String status;

    @ManyToOne  //나중에 공부
    private User user;

    @ManyToOne  //나중에 공부
    private Store store;

    @Builder
    public Review(int rating, String content, String status){
        this.rating = rating;
        this.content = content;
        this.status = status;

    }

}
