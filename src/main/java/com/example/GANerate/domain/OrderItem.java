package com.example.GANerate.domain;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_product_id")
    private DataProduct dataProduct;

    @Builder
    public OrderItem(Long id){
        this.id=id;
    }

    // Order 연관관계 편의 메서드
    public void setOrder(Order order){
        this.order=order;
        order.getOrderItems().add(this);
    }
}
