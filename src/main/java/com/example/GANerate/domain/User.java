package com.example.GANerate.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @NotNull
    private String email;

    @NotNull
    private String userPw;

    @NotNull
    private String name;

    @NotNull
    private String phoneNum;

    @Column(name = "email_auth", columnDefinition = "INT DEFAULT 0")
    private boolean emailAuth;

    //회원 포인트(자신이 올린 데이터 상품 판매시, 특정 금액 포인트)
    private Long point;

    // Autority와 User는 Many To Many 관계이기 때문에, Join Table이 필수적이다.
    // user_id, authority_name을 필드로 갖는 join 테이블이 생성된다.
    @ManyToMany
    @JoinTable(
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
    private Set<Authority> authorities;

    // 사용자가 주문한 주문 목록
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

    // 사용자가 결제한 결제 정보
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Payment> payments = new ArrayList<>();



    @Builder
    public User(Long id, String userPw, String name, String email, String phoneNum, Set<Authority> authorities, Long point){
        this.id=id;
        this.userPw=userPw;
        this.name=name;
        this.email=email;
        this.phoneNum=phoneNum;
        this.authorities = authorities;
        this.point = point;
    }

    public void addPoint(Long point){
        this.point = point;
    }

}
