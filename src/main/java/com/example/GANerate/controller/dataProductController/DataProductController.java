package com.example.GANerate.controller.dataProductController;

import com.example.GANerate.request.dateProduct.DataProductRequest;
import com.example.GANerate.response.CustomResponseEntity;
import com.example.GANerate.response.dateProduct.DataProductResponse;
import com.example.GANerate.service.dataProduct.DataProductSearchService;
import com.example.GANerate.service.dataProduct.DataProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class DataProductController {

    private final DataProductService dataProductService;
    private final DataProductSearchService dataProductSearchService;

    //페이징 처리해서 데이터 상품 목록가져오기 (최신순으로)
    @GetMapping("/v1/data-products")
    public CustomResponseEntity<Page<DataProductResponse.findDataProducts>> findDataProducts(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) @Valid
    Pageable pageable){
        return CustomResponseEntity.success(dataProductService.findDataProducts(pageable));
    }

    //선택한 카테고리 데이터 상품 조회(페이징)
    @GetMapping("/v1/data-products/category/{categoryId}")
    public CustomResponseEntity<Page<DataProductResponse.findDataProducts>> findCategoryDataProducts(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) @Valid Pageable pageable,
            @PathVariable("categoryId") Long categoryId){
        return CustomResponseEntity.success(dataProductService.findCategoryDataProducts(pageable, categoryId));
    }

    // 메인페이지(구매수 상위 3개 상품 조회)(카테고리, 이름, 생성일, 가격, 데이터 수량?)
    @GetMapping("/v1/data-products/top3")
    public CustomResponseEntity<List<DataProductResponse.findDataProducts>> findTop3Download(){
        return CustomResponseEntity.success(dataProductService.findTop3Download());
    }


    // 데이터 상품 생성(GANerate 이용) 결제후 요청해야함.
    @PostMapping("/v1/data-products")
    public CustomResponseEntity<?> createDataProduct(
            @RequestPart @Valid final DataProductRequest.createProduct request, @RequestPart MultipartFile zipFile) throws Exception {
        dataProductService.createDataProduct(request, zipFile);
        return CustomResponseEntity.success();
    }

    // 데이터 상품 판매(zip)
    @PostMapping("/v1/data-products/sale/zip")
    public CustomResponseEntity<DataProductResponse.saleDataProductZip> saleDataProductsZip(
            @RequestPart MultipartFile zipFile) throws Exception {
        return CustomResponseEntity.success(dataProductService.saleDataProductZip(zipFile));
    }

    // 데이터 상품 판매(예시 이미지)
    @PostMapping("/v1/data-products/sale/image")
    public CustomResponseEntity<List<DataProductResponse.saleDataProductImages>> saleDataProductsImages(
            @RequestPart List<MultipartFile> exampleImages) throws IOException {
        return CustomResponseEntity.success(dataProductService.saleDataProductImages(exampleImages));
    }

    // 데이터 상품 판매 폼 작성
    @PostMapping("/v1/data-products/sale")
    public CustomResponseEntity<DataProductResponse.saleDataProduct> saleDataProductsForm(
            @RequestBody @Valid DataProductRequest.saleProduct request) {
        return CustomResponseEntity.success(dataProductService.saleDataProductForm(request));
    }

    // 단일 데이터 상품 조회
    @GetMapping("/v1/data-products/{data-product-id}")
    public CustomResponseEntity<DataProductResponse.findDataProduct> findDataProduct(@PathVariable("data-product-id") Long dataProductId){
        return CustomResponseEntity.success(dataProductService.findDataProduct(dataProductId));
    }

    // 카테고리, 가격, 상품명 조건 받아서 검색 (데이터 상품 조건 검색)
    @PostMapping("/v1/data-products/filter")
    public CustomResponseEntity<Page<DataProductResponse.findDataProducts>> findDataProductsFiltered(@RequestBody @Valid DataProductRequest.filter request){
        return CustomResponseEntity.success(dataProductSearchService.findDataProductsFiltered(request));
    }
}
