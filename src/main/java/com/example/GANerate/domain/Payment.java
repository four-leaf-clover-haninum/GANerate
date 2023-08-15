package com.example.GANerate.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paymnet_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String merchantUid; // PG 사에서 생성한 주문 번호

    @Column(nullable = false, unique = true)
    private String impUid; //결제번호
//
//    @Column(nullable = false, unique = true)
//    private String paymentId; // 우리가 생성한 주문 번호ㅏ

    private String productTitle;

    @Column(nullable = false)
    private Integer amount; // 결제 금액

    //결제 방법
    @NotNull
    private String payMethod;

    @Column(nullable = false, length = 100)
    private String buyerAddr;

    @Column(nullable = false, length = 100)
    private String buyerPostcode;

    @Builder.Default
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.READY; // 결제상태

    @CreatedDate
    private LocalDateTime createAt; // 결제 요청 일시

    private LocalDateTime paidAt; // 결제 완료 일시

    private LocalDateTime failedAt; // 결제 실패 일시

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    // 상품은 여러 결제정보를 가질수 있고, 결제정보는 하나의 상품과 매핑됨.
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "data_product_id")
//    private DataProduct dataProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void setUser(User user){
        this.user=user;
        user.getPayments().add(this);
    }

}
