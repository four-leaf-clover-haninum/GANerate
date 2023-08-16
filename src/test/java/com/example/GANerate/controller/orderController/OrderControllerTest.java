package com.example.GANerate.controller.orderController;

import com.example.GANerate.response.order.OrderResponse;
import com.example.GANerate.support.docs.RestDocsTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBean(JpaMetamodelMappingContext.class)

class OrderControllerTest extends RestDocsTestSupport {

//    @Test
//    @DisplayName("결제 전 주문 확인 창")
//    public void productOrder() throws Exception {
//        //given
//        OrderResponse.ProductBuyOrder response = OrderResponse.ProductBuyOrder.builder()
//                .userId(1L)
//                .userName("kim")
//                .userEmail("abc@naver.com")
//                .dataProductId(1L)
//                .dataProductTitle("testProduct")
//                .dataProductPrice(10000L)
//                .categoryNames(List.of("패션"))
//                .zipFileOriginalFileName("originalZipFileName")
//                .zipFileSizeGb(123)
//                .dataProductSize(2000L)
//                .build();
//
//        given(orderService.productOrder(any(Long.class))).willReturn(response);
//
//        //when
//        ResultActions result = this.mockMvc.perform(
//                get("/v1/orders/{data-product-id}", 1L)
//                        .header("Authorization", "Basic dXNlcjpzZWNyZXQ=")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON)
//        );
//
//        //then
//        result.andExpect(status().isOk())
//                .andDo(
//                        restDocs.document(
//                                pathParameters(
//                                        parameterWithName("data-product-id").description("데이터 키값")
//                                ),
//                                requestHeaders(
//                                        headerWithName("Authorization").description("accessToken")
//                                ),
//                                responseFields(
//                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과코드"),
//                                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
//                                        fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("회원 키값"),
//                                        fieldWithPath("data.userName").type(JsonFieldType.STRING).description("회원 이름"),
//                                        fieldWithPath("data.userEmail").type(JsonFieldType.STRING).description("회원 이메일"),
//                                        fieldWithPath("data.dataProductId").type(JsonFieldType.NUMBER).description("데이터 키값"),
//                                        fieldWithPath("data.dataProductTitle").type(JsonFieldType.STRING).description("데이터 상품명"),
//                                        fieldWithPath("data.dataProductPrice").type(JsonFieldType.NUMBER).description("데이터 상품 가격"),
//                                        fieldWithPath("data.categoryName").type(JsonFieldType.ARRAY).description("카테고리 명 리스트"),
//                                        fieldWithPath("data.zipFileOriginalFileName").type(JsonFieldType.STRING).description("오리지널 zip 파일명"),
//                                        fieldWithPath("data.zipFileSizeGb").type(JsonFieldType.NUMBER).description("zip파일 크기"),
//                                        fieldWithPath("data.dataProductSize").type(JsonFieldType.NUMBER).description("zip 파일 내의 이미지 수")
//                                        )
//                        )
//                );
//
//    }
//
//    @Test
//    @DisplayName("결제 완료시 주문 생성")
//    public void createOrder() throws Exception {
//        //given
//        OrderResponse.CreateOrder response = OrderResponse.CreateOrder.builder()
//                .dataProductName("testProduct")
//                .dataProductId(1L)
//                .price(1000L)
//                .email("abc@naver.com")
//                .userName("kim")
//                .phoneNum("01023450123")
//                .orderId(1L)
//                .build();
//
//        given(orderService.createOrder(eq(1L))).willReturn(response);
//
//        //when
//        ResultActions result = this.mockMvc.perform(
//                post("/v1/orders/{data-product-id}", 1L)
//                        .header("Authorization", "Basic dXNlcjpzZWNyZXQ=")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON)
//        );
//
//        //then
//        result.andExpect(status().isOk())
//                .andDo(
//                        restDocs.document(
//                                pathParameters(
//                                        parameterWithName("data-product-id").description("데이터 키값")
//                                ),
//                                requestHeaders(
//                                        headerWithName("Authorization").description("accessToken")
//                                ),
//                                responseFields(
//                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과코드"),
//                                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
//                                        fieldWithPath("data.dataProductName").type(JsonFieldType.STRING).description("데이터 상품명"),
//                                        fieldWithPath("data.dataProductId").type(JsonFieldType.NUMBER).description("데이터 상품 키값"),
//                                        fieldWithPath("data.price").type(JsonFieldType.NUMBER).description("데이터 상품 가격"),
//                                        fieldWithPath("data.email").type(JsonFieldType.STRING).description("회원 이메일"),
//                                        fieldWithPath("data.userName").type(JsonFieldType.STRING).description("회원 이름"),
//                                        fieldWithPath("data.phoneNum").type(JsonFieldType.STRING).description("회원 전화번호"),
//                                        fieldWithPath("data.orderId").type(JsonFieldType.NUMBER).description("주문 키값")
//                                        )
//                        )
//                );
//    }

}