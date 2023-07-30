package com.example.GANerate.controller.dataProductController;

import com.example.GANerate.request.dateProduct.DataProductRequest;
import com.example.GANerate.response.dateProduct.DataProductResponse;
import com.example.GANerate.support.docs.RestDocsTestSupport;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipOutputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;


import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockBean(JpaMetamodelMappingContext.class)
class DataProductControllerTest extends RestDocsTestSupport {


    @Test
    @DisplayName("페이징으로 데이터 상품 목록 조회")
    void findDataProducts() throws Exception {

        List<Long> categoryId = new ArrayList<>();
        categoryId.add(1l);
        categoryId.add(2l);

        List<String> categoriesName = new ArrayList<>();
        categoriesName.add("패션");
        categoriesName.add("의료");

        // given
        List<DataProductResponse.findDataProducts> content = new ArrayList<>();
        content.add(new DataProductResponse.findDataProducts(2L, 2L, "음식 이미지 셋", 6000L, "음식이미지를 모은 자료입니다.", "test.url", LocalDateTime.of(2023, 7, 19, 12, 0, 0), null, Arrays.asList("패션")));
        content.add(new DataProductResponse.findDataProducts(1L, 0L, "얼굴 이미지 셋", 5000L, "사람의 얼굴이미지를 모은 자료입니다.", "test.url", LocalDateTime.of(2023, 7, 19, 10, 0, 0), null, Arrays.asList("패션", "건물/랜드마크")));
        content.add(new DataProductResponse.findDataProducts(3L, 100L, "동물 이미지 셋", 5000L, "동물이미지를 모은 자료입니다.", "test.url", LocalDateTime.of(2023, 7, 19, 9, 0, 0), null, new ArrayList<>()));

        // Create Pageable
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        // Create Page object
        Page<DataProductResponse.findDataProducts> pageResponse = new PageImpl<>(content, pageable, 3);

        // Mocking 서비스 응답
        given(dataProductService.findDataProducts(ArgumentMatchers.eq(pageable))).willReturn(pageResponse);


        //when
        ResultActions result = this.mockMvc.perform(
                get("/v1/data-products")
                        .param("page", "0")
                        .header("Authorization", "Basic dXNlcjpzZWNyZXQ=")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestParameters(
                                        parameterWithName("page").description("페이지 번호 (0-based index)")
                                ),
                                responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),

                                        fieldWithPath("data.content[].id").type(JsonFieldType.NUMBER).description("키값"),
                                        fieldWithPath("data.content[].buyCnt").type(JsonFieldType.NUMBER).description("현재까지 구매 수량"),
                                        fieldWithPath("data.content[].title").type(JsonFieldType.STRING).description("상품명"),
                                        fieldWithPath("data.content[].price").type(JsonFieldType.NUMBER).description("가격"),
                                        fieldWithPath("data.content[].description").type(JsonFieldType.STRING).description("설명"),
                                        fieldWithPath("data.content[].imageUrl").type(JsonFieldType.STRING).description("썸네일 이미지 url"),
                                        fieldWithPath("data.content[].createdAt").type(JsonFieldType.STRING).description("상품 생성일자"),
                                        fieldWithPath("data.content[].categoryId").type(JsonFieldType.NUMBER).optional().description("상품 카테고리 키값들"),
                                        fieldWithPath("data.content[].categoriesName").type(JsonFieldType.ARRAY).description("상품 카테고리 명칭들"),

                                        fieldWithPath("data.pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                                        fieldWithPath("data.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬되지 않은 경우 여부"),
                                        fieldWithPath("data.pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보가 비어있는지 여부"),
                                        fieldWithPath("data.pageable.pageNumber").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                        fieldWithPath("data.pageable.pageSize").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                        fieldWithPath("data.pageable.offset").type(JsonFieldType.NUMBER).description("현재 페이지의 시작 위치"),
                                        fieldWithPath("data.pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 여부"),
                                        fieldWithPath("data.pageable.unpaged").type(JsonFieldType.BOOLEAN).description("페이징되지 않은 경우 여부"),
                                        fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                                        fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 요소 수"),
                                        fieldWithPath("data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                                        fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지의 요소 수"),
                                        fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("현재 페이지 크기"),
                                        fieldWithPath("data.number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                        fieldWithPath("data.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                                        fieldWithPath("data.sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬되지 않은 경우 여부"),
                                        fieldWithPath("data.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보가 비어있는지 여부"),
                                        fieldWithPath("data.first").type(JsonFieldType.BOOLEAN).description("첫 번째 페이지 여부"),
                                        fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN).description("데이터가 비어있는지 여부")
                                )
                        )
                )
        ;
    }

    @Test
    @DisplayName("유저가 보유한 데이터 판매")
    //카테고리 이슈 해결하고 다시 수정
    void saleDataProducts() throws Exception {
        //given
        Long userId =1l;
        List<Long> categoryId = new ArrayList<>();
        categoryId.add(1l);
        categoryId.add(2l);

        //요청 dto
        DataProductRequest.saleProduct requestDto = DataProductRequest.saleProduct.builder()
                .title("test 상품").price(10000l).description("test 상품 입니다.").categoryIds(List.of(1l,2l))
                .build();

        String request = objectMapper.writeValueAsString(requestDto);

        String path="스크린샷 2023-07-14 오후 2.50.04.png";
        InputStream inputStream = new ClassPathResource(path).getInputStream();
        // 이미지 파일들 생성
        MockMultipartFile imageFile1 = new MockMultipartFile(
                "imageFiles", "스크린샷 2023-07-14 오후 2.50.04.png", "image/png", inputStream.readAllBytes());

        byte[] dummyZipBytes = new byte[]{};
        MockMultipartFile zipFile = new MockMultipartFile(
                "zipFile",
                "dummy.zip",
                "application/zip",
                dummyZipBytes
        );

        // 응답
        DataProductResponse.saleDataProduct response = DataProductResponse.saleDataProduct.builder()
                .id(1l)
                .build();

        given(dataProductService.saleDataProduct(userId,zipFile,List.of(imageFile1), requestDto)).willReturn(response);

        ResultActions result = this.mockMvc.perform(
                multipart("/v1/data-products/sale")
                        .file("zipFile", zipFile.getBytes())
                        .file("exampleImages", imageFile1.getBytes())
                        .content(request)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header("Authorization", "Bearer dXNldasdasdasdasdasdgfgegrtjyrutwcjpzZWNyZXQ=")
                        .accept(MediaType.APPLICATION_JSON)
        );

        result.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestParts(
                                        partWithName("zipFile").description("이미지 데이터 상품 zip"),
                                        partWithName("exampleImages").description("예시 이미지(썸네일)")
                                ),
                                requestFields(
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("상품 명"),
                                        fieldWithPath("price").type(JsonFieldType.NUMBER).description("상품 가격"),
                                        fieldWithPath("description").type(JsonFieldType.STRING).description("상품 설먕"),
                                        fieldWithPath("categoryIds[]").type(JsonFieldType.ARRAY).description("속한 카테고리").optional()
                                        ),
                                responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("생성된 상품 id")
                                )

                        )
                )
        ;
    }

    @Test
    void findDataProduct() {
    }
}