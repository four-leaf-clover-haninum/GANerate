package com.example.GANerate.domain;

import com.example.GANerate.enumuration.OrderStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="orders")
public class Order extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull
    private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    List<OrderItem> orderItems = new ArrayList<>();

    //연관관계 편의 메서드
    public void setUser(User user){
        this.user=user;
        user.getOrders().add(this);
    }

    //zip 생성되면 상태 Done으로 변경
    public void setStatus(OrderStatus orderStatus){
        this.status=orderStatus;
    }

    @Builder
    public Order(Long id, OrderStatus orderStatus){
        this.id = id;
        this.status = orderStatus;
    }
}
