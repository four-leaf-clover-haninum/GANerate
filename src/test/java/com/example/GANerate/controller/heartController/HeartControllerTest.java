package com.example.GANerate.controller.heartController;

import com.example.GANerate.domain.DataProduct;
import com.example.GANerate.domain.Heart;
import com.example.GANerate.domain.User;
import com.example.GANerate.response.heart.HeartResponse;
import com.example.GANerate.support.docs.RestDocsTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBean(JpaMetamodelMappingContext.class)
class HeartControllerTest extends RestDocsTestSupport {

    @Test
    @DisplayName("상품 좋아요")
    void like() throws Exception {

        Long userId = 1L;
        Long dataProductId = 1L;

        User user = new User(userId, "test", "test", "test", "test", null, 100L);
        DataProduct dataProduct = new DataProduct(dataProductId, 1L, "test", 1000L, "testing", 12L);
        Heart heart = new Heart(1L, user, dataProduct);

        HeartResponse response = HeartResponse.builder()
                .heartId(1L)
                .build();

        given(heartService.like(eq(1L))).willReturn(response);

        ResultActions result = this.mockMvc.perform(
                post("/v1/hearts/{data-product-id}", dataProductId)
                        .header("Authorization", "Basic dXNlcjpzZWNyZXQ=")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        result.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("data-product-id").description("데이터 키값")
                                ),
                                requestHeaders(
                                        headerWithName("Authorization").description("accessToken")
                                ),
                                responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                        fieldWithPath("data.heartId").type(JsonFieldType.NUMBER).description("좋아요 키값")
                                )
                        )
                );
    }

    @Test
    @DisplayName("상품 좋아요 취소")
    void unlike() throws Exception {
        ResultActions result = this.mockMvc.perform(
                delete("/v1/hearts/{data-product-id}", 1L)
                        .header("Authorization", "Basic dXNlcjpzZWNyZXQ=")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("data-product-id").description("데이터 키값")
                                ),
                                responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("결과 데이터")
                                )
                        )
                );
    }
}