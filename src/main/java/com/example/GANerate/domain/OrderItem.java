package com.example.GANerate.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
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
    public OrderItem(Long id, Order order, DataProduct dataProduct){
        this.id=id;
        this.order=order;
        this.dataProduct=dataProduct;
    }

    // Order 연관관계 편의 메서드
    public void setOrder(Order order){
        this.order=order;
        order.getOrderItems().add(this);
    }

    public void setDataProduct(DataProduct dataProduct){
        this.dataProduct=dataProduct;
    }
}
