package com.example.GANerate.response.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class PaymentResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class CreatePayment{
        private Long paymentId;
        private String merchantUid;
        private String productTitle;
        private Integer amount;
        private LocalDateTime createAt;
        private Long userId;
        private String userName;
        private Long orderId;
        private Long productId;
    }

}
