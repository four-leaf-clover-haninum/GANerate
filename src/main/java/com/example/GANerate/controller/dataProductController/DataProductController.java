package com.example.GANerate.controller.dataProductController;

import com.example.GANerate.request.dateProduct.DataProductRequest;
import com.example.GANerate.response.CustomResponseEntity;
import com.example.GANerate.response.dateProduct.DataProductResponse;
import com.example.GANerate.service.dataProduct.DataProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class DataProductController {

    private final DataProductService dataProductService;
    //페이징 처리해서 데이터 상품 목록가져오기 (최신순으로)
    @GetMapping("/v1/data-products")
    public CustomResponseEntity<Page<DataProductResponse.findDataProducts>> findDataProducts(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) @Valid
    Pageable pageable)
    {
        return CustomResponseEntity.success(dataProductService.findDataProducts(pageable));
    }

    //선택한 카테고리 데이터 상품 조회(페이징)
    @GetMapping("/v1/data-products/category/{categoryId}")
    public CustomResponseEntity<Page<DataProductResponse.findDataProducts>> findCategoryDataProducts(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) @Valid Pageable pageable,
            @PathVariable("categoryId") Long categoryId){
        return CustomResponseEntity.success(dataProductService.findCategoryDataProducts(pageable, categoryId));
    }

    // 메인페이지(다운로드 상위 3개 상품 조회)(카테고리, 이름, 생성일, 가격, 데이터 수량?)
    @GetMapping("/v1/data-products/top3")
    public CustomResponseEntity<List<DataProductResponse.findDataProducts>> findTop3Download(){
        return CustomResponseEntity.success(dataProductService.findTop3Download());
    }


    // 데이터 상품 생성(GANERTE 이용)
//    @PostMapping("/dataProduct")
//    public CustomResponseEntity<DataProductResponse.createDataProduct> createDataProduct(
//            @AuthenticationPrincipal final Long userId, @RequestPart final DataProductRequest.createDataProduct request, @RequestPart MultipartFile zipfile){
//        return CustomResponseEntity.success(dataProductService.createDataProduct(userId, request, zipfile));
//    }폼

    //데이터 상품 판매(그냥 폼을 이용해 올리기)
    @PostMapping("/v1/data-products/sale")
    public CustomResponseEntity<DataProductResponse.saleDataProduct> saleDataProducts(
            @AuthenticationPrincipal Long userId, @RequestPart MultipartFile zipFile,
            @RequestPart List<MultipartFile> exampleImages, @RequestPart @Valid DataProductRequest.saleProduct request) {
        return CustomResponseEntity.success(dataProductService.saleDataProduct(userId, zipFile, exampleImages, request));
    }


    // 단일 데이터 상품 조회
    @GetMapping("/v1/data-products/{data-product-id}")
    public CustomResponseEntity<DataProductResponse.findDataProduct> findDataProduct(@PathVariable("data-product-id") Long dataProductId){
        return CustomResponseEntity.success(dataProductService.findDataProduct(dataProductId));
    }

    // 데이터 상품 구매(아님 Order에서 주문 주문내역 조회, 주문 취소 등을 개발?

    // 카테고리, 가격, 상품명 조건 받아서 검색
//    @GetMapping("/v1/data-products/filter")
//    public CustomResponseEntity<Page<DataProductResponse.findDataProducts>> findDataProductsFiltered(@RequestBody DataProductRequest.filter request
//            //페이지 정보를 dto에 넣어서 보내자
//                                                                                                     ){
//        return CustomResponseEntity.success(dataProductService.findDataProductsFiltered(request));
//    }

    // 구매한 데이터 상품 다운로드
}
