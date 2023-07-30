package com.example.GANerate.controller.categoryController;

import com.example.GANerate.response.category.CategoryResponse;
import com.example.GANerate.support.docs.RestDocsTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBean(JpaMetamodelMappingContext.class)
class CategoryControllerTest extends RestDocsTestSupport {

    @Test
    @DisplayName("카테고리 목록 조회")
    void findCategories() throws Exception {

        // given
        List<CategoryResponse.findCategories> response
                = List.of(new CategoryResponse.findCategories(1l,1,"패션"));

        given(categoryService.findCategories()).willReturn(response);

        //when
        ResultActions result = this.mockMvc.perform(
                get("/v1/categories")
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                        fieldWithPath("data[].categoryId").type(JsonFieldType.NUMBER).description("카테고리 키값"),
                                        fieldWithPath("data[].categoryCode").type(JsonFieldType.NUMBER).description("카테고리 코드"),
                                        fieldWithPath("data[].title").type(JsonFieldType.STRING).description("카테고리 명칭")

                                )
                        )
                )
        ;
    }
}