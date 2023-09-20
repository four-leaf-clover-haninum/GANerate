package com.example.GANerate.controller;

import com.example.GANerate.response.payment.PaymentResponse;
import com.example.GANerate.service.PaymentService;
import com.example.GANerate.support.docs.RestDocsTestSupport;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBean(JpaMetamodelMappingContext.class)
class PaymentControllerTest extends RestDocsTestSupport {

    @Test
    @DisplayName("결제정보 검증")
    public void verifyIamport() throws Exception {
        // given
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("imp_uid", "홍길동_20230812");
        requestBody.put("dataProductId", "123");
        requestBody.put("amount", "1000");

        PaymentResponse.CreatePayment response = PaymentResponse.CreatePayment.builder()
                .paymentId(1L)
                .merchantUid("abc_123")
                .productTitle("테스트 상품")
                .amount(1000)
                .createAt(LocalDateTime.now())
                .userId(1L)
                .orderId(1L)
                .userName("홍길동").build();

        given(paymentService.verifyIamportService(eq("홍길동_20230812"), eq(1000), eq(123L), any(IamportClient.class))).willReturn(response);

        // Perform the request and document it
        ResultActions result = this.mockMvc.perform(
                post("/v1/payments/verifyIamport")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody))
                        .accept(MediaType.APPLICATION_JSON)
        );

        result.andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("imp_uid").type(JsonFieldType.STRING).description("아임포트 imp_uid"),
                                fieldWithPath("dataProductId").type(JsonFieldType.STRING).description("데이터 상품 id"),
                                fieldWithPath("amount").type(JsonFieldType.STRING).description("유저가 결제한 금액")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                fieldWithPath("data.paymentId").type(JsonFieldType.NUMBER).description("결제 id"),
                                fieldWithPath("data.merchantUid").type(JsonFieldType.STRING).description("PG사에서 생성한 주문 id"),
                                fieldWithPath("data.productTitle").type(JsonFieldType.STRING).description("상품명"),
                                fieldWithPath("data.amount").type(JsonFieldType.NUMBER).description("결제 금액"),
                                fieldWithPath("data.createAt").type(JsonFieldType.STRING).description("결제일자"),
                                fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("회원 id"),
                                fieldWithPath("data.userName").type(JsonFieldType.STRING).description("회원명"),
                                fieldWithPath("data.orderId").type(JsonFieldType.NUMBER).description("주문 id")
                                )
                        )
                );
    }
}