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
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.request.RequestPartsSnippet;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipOutputStream;

import static com.example.GANerate.config.RestDocsConfig.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
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
        content.add(new DataProductResponse.findDataProducts(2L, 2L, "음식 이미지 셋", 6000L, "음식이미지를 모은 자료입니다.", "test.url", LocalDateTime.of(2023, 7, 19, 12, 0, 0), List.of(1L,2L), Arrays.asList("패션")));
        content.add(new DataProductResponse.findDataProducts(1L, 0L, "얼굴 이미지 셋", 5000L, "사람의 얼굴이미지를 모은 자료입니다.", "test.url", LocalDateTime.of(2023, 7, 19, 10, 0, 0), List.of(4L,6L), Arrays.asList("패션", "건물/랜드마크")));
        content.add(new DataProductResponse.findDataProducts(3L, 100L, "동물 이미지 셋", 5000L, "동물이미지를 모은 자료입니다.", "test.url", LocalDateTime.of(2023, 7, 19, 9, 0, 0), List.of(2L), new ArrayList<>()));

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
                                        parameterWithName("page").description("페이지 번호 (1번 부터)")
                                ),
                                responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),

                                        fieldWithPath("data.content[].dataProductId").type(JsonFieldType.NUMBER).description("데이터 상품 키값"),
                                        fieldWithPath("data.content[].buyCnt").type(JsonFieldType.NUMBER).description("해당 상품 구매된 수량"),
                                        fieldWithPath("data.content[].title").type(JsonFieldType.STRING).description("상품명"),
                                        fieldWithPath("data.content[].price").type(JsonFieldType.NUMBER).description("가격"),
                                        fieldWithPath("data.content[].description").type(JsonFieldType.STRING).description("설명"),
                                        fieldWithPath("data.content[].imageUrl").type(JsonFieldType.STRING).description("썸네일 이미지 url"),
                                        fieldWithPath("data.content[].createdAt").type(JsonFieldType.STRING).description("상품 생성일자"),
                                        fieldWithPath("data.content[].categoryIds").type(JsonFieldType.ARRAY).optional().description("상품 카테고리 키값"),
                                        fieldWithPath("data.content[].categoryNames").type(JsonFieldType.ARRAY).description("상품 카테고리 명칭"),

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
    @DisplayName("선택한 카테고리 상품 페이징")
    void findCategoryDataProducts() throws Exception {

        List<Long> categoryId = new ArrayList<>();
        categoryId.add(1l);
        categoryId.add(2l);

        List<String> categoriesName = new ArrayList<>();
        categoriesName.add("패션");
        categoriesName.add("의료");

        DataProductResponse.findDataProducts dto1 = DataProductResponse.findDataProducts.builder()
                .dataProductId(1L)
                .title("test1")
                .price(1000L)
                .createdAt(LocalDateTime.now())
                .categoryIds(List.of(1L))
                .categoryNames(List.of("패션"))
                .buyCnt(2L)
                .description("testing1")
                .imageUrl("http://test1.com").build();

        DataProductResponse.findDataProducts dto2 = DataProductResponse.findDataProducts.builder()
                .dataProductId(2L)
                .title("test2")
                .price(1200L)
                .createdAt(LocalDateTime.now())
                .categoryIds(List.of(2L))
                .categoryNames(List.of("의료"))
                .buyCnt(21L)
                .description("testing2")
                .imageUrl("http://test2.com").build();

        DataProductResponse.findDataProducts dto3 = DataProductResponse.findDataProducts.builder()
                .dataProductId(3L)
                .title("test3")
                .price(3200L)
                .createdAt(LocalDateTime.now())
                .categoryIds(List.of(2L))
                .categoryNames(List.of("의료"))
                .buyCnt(211L)
                .description("testing3")
                .imageUrl("http://test3.com").build();

        List<DataProductResponse.findDataProducts> content = List.of(dto2, dto3);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        // Create Page object
        Page<DataProductResponse.findDataProducts> pageResponse = new PageImpl<>(content, pageable, 2);

        // Mocking 서비스 응답
        given(dataProductService.findCategoryDataProducts(ArgumentMatchers.eq(pageable), any(Long.class))).willReturn(pageResponse);

        //when
        ResultActions result = this.mockMvc.perform(
                get("/v1/data-products/category/{categoryId}", 2L)
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
                                pathParameters(
                                        parameterWithName("categoryId").description("조회할 카테고리 id")
                                ),
                                responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),

                                        fieldWithPath("data.content[].dataProductId").type(JsonFieldType.NUMBER).description("데이터 상품 키값"),
                                        fieldWithPath("data.content[].buyCnt").type(JsonFieldType.NUMBER).description("해당 상품 구매된 수량"),
                                        fieldWithPath("data.content[].title").type(JsonFieldType.STRING).description("상품명"),
                                        fieldWithPath("data.content[].price").type(JsonFieldType.NUMBER).description("가격"),
                                        fieldWithPath("data.content[].description").type(JsonFieldType.STRING).description("설명"),
                                        fieldWithPath("data.content[].imageUrl").type(JsonFieldType.STRING).description("썸네일 이미지 url"),
                                        fieldWithPath("data.content[].createdAt").type(JsonFieldType.STRING).description("상품 생성일자"),
                                        fieldWithPath("data.content[].categoryIds").type(JsonFieldType.ARRAY).optional().description("상품 카테고리 키값"),
                                        fieldWithPath("data.content[].categoryNames").type(JsonFieldType.ARRAY).description("상품 카테고리 명칭"),

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
    @DisplayName("메인페이지 구매 top3")
    void findCategoryDataProductsTop3() throws Exception{
        List<Long> categoryId = new ArrayList<>();
        categoryId.add(1l);
        categoryId.add(2l);

        List<String> categoriesName = new ArrayList<>();
        categoriesName.add("패션");
        categoriesName.add("의료");

        DataProductResponse.findDataProducts dto1 = DataProductResponse.findDataProducts.builder()
                .dataProductId(1L)
                .title("test1")
                .price(1000L)
                .createdAt(LocalDateTime.now())
                .categoryIds(List.of(1L))
                .categoryNames(List.of("패션"))
                .buyCnt(2L)
                .description("testing1")
                .imageUrl("http://test1.com").build();

        DataProductResponse.findDataProducts dto2 = DataProductResponse.findDataProducts.builder()
                .dataProductId(2L)
                .title("test2")
                .price(1200L)
                .createdAt(LocalDateTime.now())
                .categoryIds(List.of(2L))
                .categoryNames(List.of("의료"))
                .buyCnt(21L)
                .description("testing2")
                .imageUrl("http://test2.com").build();

        DataProductResponse.findDataProducts dto3 = DataProductResponse.findDataProducts.builder()
                .dataProductId(3L)
                .title("test3")
                .price(3200L)
                .createdAt(LocalDateTime.now())
                .categoryIds(List.of(2L))
                .categoryNames(List.of("의료"))
                .buyCnt(211L)
                .description("testing3")
                .imageUrl("http://test3.com").build();

        List<DataProductResponse.findDataProducts> response = List.of(dto1,dto2,dto3);

        given(dataProductService.findTop3Download()).willReturn(response);

        //when
        ResultActions result = this.mockMvc.perform(
                get("/v1/data-products/top3")
                        .header("Authorization", "Basic dXNlcjpzZWNyZXQ=")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                        fieldWithPath("data[].dataProductId").type(JsonFieldType.NUMBER).description("데이터 상품 키값"),
                                        fieldWithPath("data[].buyCnt").type(JsonFieldType.NUMBER).description("해당 상품 구매된 수량"),
                                        fieldWithPath("data[].title").type(JsonFieldType.STRING).description("상품명"),
                                        fieldWithPath("data[].price").type(JsonFieldType.NUMBER).description("가격"),
                                        fieldWithPath("data[].description").type(JsonFieldType.STRING).description("설명"),
                                        fieldWithPath("data[].imageUrl").type(JsonFieldType.STRING).description("썸네일 이미지 url"),
                                        fieldWithPath("data[].createdAt").type(JsonFieldType.STRING).description("상품 생성일자"),
                                        fieldWithPath("data[].categoryIds").type(JsonFieldType.ARRAY).optional().description("상품 카테고리 키값"),
                                        fieldWithPath("data[].categoryNames").type(JsonFieldType.ARRAY).description("상품 카테고리 명칭")
                                )

                        )
                )
        ;
    }

    @Test
    @DisplayName("데이터 상품 생성(GANerate)")
    void createDataProduct() throws Exception {
        // Sample request data
//        DataProductRequest.createProduct request = DataProductRequest.createProduct.builder()
//                .orderId(1L)
//                .title("Product Title")
//                .description("Product Description")
//                .dataSize(10L)
//                .categoryIds(Arrays.asList(1L, 2L))
//                .build();

        DataProductResponse.createDataProduct response = DataProductResponse.createDataProduct.builder()

                .title("Product Title")
                .description("Product Description")
                .imageUrl("http://test1.com")
                .price(1000L)
                .build();

        String content = objectMapper.writeValueAsString(new DataProductRequest.createProduct(1L, "Product Title", "Product Description", 10L, Arrays.asList(1L, 2L)));

//        Path zipFilePath = Paths.get("src/test/resources/아카이브 복사본.zip");
        MockMultipartFile zipFile = new MockMultipartFile(
                "zipFile",
                "test.zip",
                "application/zip",
                "test data".getBytes()
        );

        MockMultipartFile json = new MockMultipartFile(
                "request", "jsondata", "application/json", content.getBytes(StandardCharsets.UTF_8));

//        given(dataProductService.createDataProduct(any(DataProductRequest.createProduct.class), any(MultipartFile.class))).willReturn(response);


        // Perform the request and document it
        ResultActions result = this.mockMvc.perform(
                multipart("/v1/data-products")
                        .file(zipFile)
                        .file(json)
                        .contentType(MediaType.MULTIPART_MIXED)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        );

        result.andExpect(status().isOk())
                .andDo(restDocs.document(
                        relaxedRequestParts(
                                partWithName("zipFile").description("생성시 사용할 예시 zip 파일"),
                                partWithName("request").description("상품 생성 요청 폼(orderId, title, description, dataSize, categoryIds)")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                fieldWithPath("data").description("데이터")
                        )

                ));
    }



    @Test
    @DisplayName("유저가 보유한 데이터 zip 업로드")
    void saleDataProductsZip() throws Exception {
        //given

        DataProductResponse.saleDataProductZip response = DataProductResponse.saleDataProductZip.builder()
                .dataSize(100L)
                .zipFileUrl("http://test.testing")
                .build();

        // MockMultipartFile 생성
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "zipFile",                        // 파라미터 이름
                "test.zip",                       // 파일 이름
                "application/zip",                // 컨텐츠 타입
                "test data".getBytes()            // 파일 데이터 (바이트 배열)
        );

        given(dataProductService.saleDataProductZip(eq(mockMultipartFile))).willReturn(response);

        // when
        ResultActions result = this.mockMvc.perform(
                MockMvcRequestBuilders.multipart("/v1/data-products/sale/zip")
                        .file(mockMultipartFile)
                        .header("Authorization", "Basic dXNlcjpzZWNyZXQ=")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                relaxedRequestParts( // (2)
                                        partWithName("zipFile").description("판매시 업로드할 zipFile")), // (3)
                                responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                        fieldWithPath("data.dataSize").type(JsonFieldType.NUMBER).description("zip 내부의 파일 수"),
                                        fieldWithPath("data.zipFileUrl").type(JsonFieldType.STRING).description("zip 다운로드 url")
                                )
                        )
                )
        ;
    }

    @Test
    @DisplayName("유저가 보유한 예시 이미지 업로드")
    void saleDataProductsImages() throws Exception {
        //given
        DataProductResponse.saleDataProductImages dto1 = DataProductResponse.saleDataProductImages.builder()
                .imageUrl("http://test1.testing").build();

        DataProductResponse.saleDataProductImages dto2 = DataProductResponse.saleDataProductImages.builder()
                .imageUrl("http://test2.testing").build();

        List<DataProductResponse.saleDataProductImages> response = List.of(dto1,dto2);

        Path path = Paths.get("src/test/resources/스크린샷 2023-07-14 오후 2.50.04.png");

        MockMultipartFile mockMultipartFile1 = new MockMultipartFile(
                "exampleImages",                        // 파라미터 이름
                "exampleImages.png",                       // 파일 이름
                "image/png",                // 컨텐츠 타입
                "test data".getBytes()           // 파일 데이터 (바이트 배열)
        );

        MockMultipartFile mockMultipartFile2 = new MockMultipartFile(
                "exampleImages",                // 파라미터 이름
                "example.png",                  // 파일 이름
                "image/png",                    // 컨텐츠 타입
                "test data".getBytes()        // 파일 데이터 (바이트 배열)
        );
        List<MultipartFile> exampleImages = List.of(mockMultipartFile1, mockMultipartFile2);


        given(dataProductService.saleDataProductImages(eq(exampleImages))).willReturn(response);

        // when
        ResultActions result = this.mockMvc.perform(
                MockMvcRequestBuilders.multipart("/v1/data-products/sale/image")
                        .file(mockMultipartFile1)
                        .file(mockMultipartFile2)
                        .header("Authorization", "Basic dXNlcjpzZWNyZXQ=")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                relaxedRequestParts( // (2)
                                        partWithName("exampleImages").description("판매시 업로드 할 imageFile 리스트")), // (3)
                                responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                        fieldWithPath("data[].imageUrl").type(JsonFieldType.STRING).description("image 다운로드 url")
                                )
                        )
                )
        ;

    }

    @Test
    @DisplayName("유저가 보유한 데이터 판매 폼 업로드")
    void saleDataProductsForm() throws Exception {
        // given
        DataProductResponse.saleDataProduct response = DataProductResponse.saleDataProduct.builder()
                .id(1L).build();

        List<Long> categoryIds = List.of(1L,2L);
        List<String> imageUrls = List.of("http://test1.com","http://test2.com");

        DataProductRequest.saleProduct request = DataProductRequest.saleProduct.builder()
                .title("test 상품").dataSize(10L).description("test 상품입니다").categoryIds(categoryIds).zipFileUrl("http://ziptest").imageUrls(imageUrls).price(1000L).build();

        given(dataProductService.saleDataProductForm(any(DataProductRequest.saleProduct.class))).willReturn(response);

        // when
        ResultActions result = this.mockMvc.perform(
                post("/v1/data-products/sale")
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Basic dXNlcjpzZWNyZXQ=")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );
        // then
        result.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestFields(
                                        fieldWithPath("title").description("상품명"),
                                        fieldWithPath("dataSize").description("zip 내부 파일 개수"),
                                        fieldWithPath("description").description("상품 설명"),
                                        fieldWithPath("categoryIds").description("상품이 속하는 카테고리"),
                                        fieldWithPath("price").description("상품가격"),
                                        fieldWithPath("zipFileUrl").description("zip 파일 url"),
                                        fieldWithPath("imageUrls").description("예시 사진 url")
                                )
                                ,responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("생성된 상품의 키값")
                                )
                        )
                )
        ;
    }



    @Test
    @DisplayName("상품 상세 조회(단건 조회)")
    void findDataProduct() throws Exception{

        //given
        List<Long> categoryId = new ArrayList<>();
        categoryId.add(1l);
        categoryId.add(2l);

        List<String> categoriesName = new ArrayList<>();
        categoriesName.add("패션");
        categoriesName.add("의료");

        List<String> imageUrl = List.of("http://test1.com","http://test2.com");

        DataProductResponse.findDataProduct response = DataProductResponse.findDataProduct.builder()
                .dataProductId(1L)
                .dataSize(10L)
                .buyCnt(240L)
                .title("test 상품")
                .categoryNames(categoriesName)
                .categoryIds(categoryId)
                .createdAt(LocalDateTime.now())
                .description("테스트 상품 설명")
                .zipfileName("테스트.zip")
                .zipfileSize(4000L)
                .imageUrl(imageUrl)
                .price(20000L)
                .build();

        given(dataProductService.findDataProduct(eq(1L))).willReturn(response);

        //when
        ResultActions result = this.mockMvc.perform(
                get("/v1/data-products/{data-product-id}", 1L)
                        .header("Authorization", "Basic dXNlcjpzZWNyZXQ=")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("data-product-id").description("조회할 데이터 상품 id")
                                ),
                                responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                        fieldWithPath("data.dataProductId").type(JsonFieldType.NUMBER).description("키값"),
                                        fieldWithPath("data.dataSize").type(JsonFieldType.NUMBER).description("zip 내부 파일 수량"),
                                        fieldWithPath("data.buyCnt").type(JsonFieldType.NUMBER).description("현재까지 구매 수량"),
                                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("상품명"),
                                        fieldWithPath("data.price").type(JsonFieldType.NUMBER).description("가격"),
                                        fieldWithPath("data.description").type(JsonFieldType.STRING).description("설명"),
                                        fieldWithPath("data.imageUrl[]").type(JsonFieldType.ARRAY).description("데이터 상품 이미지 URL"),
                                        fieldWithPath("data.categoryNames[]").type(JsonFieldType.ARRAY).description("데이터 상품 카테고리 이름"),
                                        fieldWithPath("data.categoryIds[]").type(JsonFieldType.ARRAY).description("데이터 상품 카테고리 키값"),
                                        fieldWithPath("data.zipfileName").type(JsonFieldType.STRING).optional().description("zip파일 이름"),
                                        fieldWithPath("data.zipfileSize").type(JsonFieldType.NUMBER).description("zip 파일 크기(GB)"),
                                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("데이터 상품 생성일시")
                                )
                        )
                )
        ;
    }

//    @Test
//    @DisplayName("GANerate를 이용하여 데이터 상품 생성 요청")
//
//
    @Test
    @DisplayName("데이터 상품 조건 검색")
    public void findDataProductsFiltered()throws Exception{
        // given
        DataProductRequest.filter request = DataProductRequest.filter.builder()
                .page(0)
                .title("test")
                .categoryIds(List.of(1L))
                .maxPrice(10000L)
                .minPrice(100L)
                .build();

        DataProductResponse.findDataProducts response1 = DataProductResponse.findDataProducts.builder()
                .dataProductId(1L)// 데이터 상품 id
                .price(1000L)
                .title("test1")
                .buyCnt(12L)
                .categoryNames(List.of("패션","의료"))
                .categoryIds(List.of(1L,2L))
                .imageUrl("testimage")
                .description("test1 상품입니다.")
                .createdAt(LocalDateTime.now())
                .build();

        DataProductResponse.findDataProducts response2 = DataProductResponse.findDataProducts.builder()
                .dataProductId(2L)// 데이터 상품 id
                .price(2000L)
                .title("test1")
                .buyCnt(3L)
                .categoryNames(List.of("패션"))
                .categoryIds(List.of(1L))
                .imageUrl("testimage")
                .description("test2 상품입니다.")
                .createdAt(LocalDateTime.now())
                .build();

        List<DataProductResponse.findDataProducts> content = List.of(response1, response2);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        Page<DataProductResponse.findDataProducts> pageResponse = new PageImpl<>(content, pageable, 2);

        given(dataProductSearchService.findDataProductsFiltered(any(request.getClass()))).willReturn(pageResponse);

        //when
        ResultActions result = this.mockMvc.perform(
                post("/v1/data-products/filter")
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Basic dXNlcjpzZWNyZXQ=")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestFields(
                                        fieldWithPath("title").description("검색하려는 상품명(해당 문자가 포함된 상품 검색)"),
                                        fieldWithPath("minPrice").description("최소 금액"),
                                        fieldWithPath("maxPrice").description("최대 금액"),
                                        fieldWithPath("page").description("페이지 번호"),
                                        fieldWithPath("categoryIds").description("카테고리 아이디들")
                                ),
                                responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),

                                        fieldWithPath("data.content[].dataProductId").type(JsonFieldType.NUMBER).description("데이터 상품 키값"),
                                        fieldWithPath("data.content[].buyCnt").type(JsonFieldType.NUMBER).description("현재까지 구매 수량"),
                                        fieldWithPath("data.content[].title").type(JsonFieldType.STRING).description("상품명"),
                                        fieldWithPath("data.content[].price").type(JsonFieldType.NUMBER).description("가격"),
                                        fieldWithPath("data.content[].description").type(JsonFieldType.STRING).description("설명"),
                                        fieldWithPath("data.content[].imageUrl").type(JsonFieldType.STRING).description("썸네일 이미지 url"),
                                        fieldWithPath("data.content[].createdAt").type(JsonFieldType.STRING).description("상품 생성일자"),
                                        fieldWithPath("data.content[].categoryIds").type(JsonFieldType.ARRAY).optional().description("상품 카테고리 키값"),
                                        fieldWithPath("data.content[].categoryNames").type(JsonFieldType.ARRAY).description("상품 카테고리 명칭"),

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
}