package com.example.delivery_app.domain.review.entity;


import com.example.delivery_app.common.entity.BaseEntity;
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
public class Review extends BaseEntity {

    @Id     //테이블의 pk(대표컬럼하나)값 지정 (속성, 필드값)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동으로 1씩 증가
    private Long id;

    @Column(nullable = false)
    private int rating;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false) //순수한 클래스의 컬럼
    private boolean status;

    @ManyToOne(fetch = FetchType.LAZY)  // many= 지금 이 테이블 명, one = 지금 선언한 user (nㄷ1 관계)
    @JoinColumn(nullable = false, name = "user_id") // join해 온 테이블 객체
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) // Lazy: 지연 로딩, 필요로할때만 가져온다.
    @JoinColumn(nullable = false, name = "store_id")
    private Store store;

    @Builder
    public Review(Long id, int rating, String content, boolean status){
        this.id = id;
        this.rating = rating;
        this.content = content;
        this.status = status;

    }

}
