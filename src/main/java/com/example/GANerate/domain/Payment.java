package com.example.GANerate.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paymnet_id")
    private Long id;

    //결제 번호
    @NotNull
    private Long paymentsNo;

    //결제 방법
    @NotNull
    private String payMethod;

    //아임포트 주문 번호
    @NotNull
    private String impUid;

    // 구매 번호
    @NotNull
    private String merchantUid;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_product_id")
    private DataProduct dataProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Payment(Long paymentsNo, String payMethod, String impUid, String merchantUid){
        this.paymentsNo=paymentsNo;
        this.payMethod=payMethod;
        this.impUid=impUid;
        this.merchantUid=merchantUid;
    }
}
