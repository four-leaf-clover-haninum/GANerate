package com.example.GANerate.response.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PaymentResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class CreatePayment{

    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class VerifyPayment {
    }
}
