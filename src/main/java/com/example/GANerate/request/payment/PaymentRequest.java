package com.example.GANerate.request.payment;

import com.example.GANerate.domain.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class PaymentRequest {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreatePayment{
        private String imp_uid;
        private String merchant_uid;
        private BigDecimal amount;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VerifyPayment{
        @NotNull(message = "주문 번호는 필수입니다.")
        private String merchant_uid;

    }
}
