package com.example.GANerate.controller.dataProductController;

import com.example.GANerate.response.CustomResponseEntity;
import com.example.GANerate.response.dateProduct.DataProductResponse;
import com.example.GANerate.service.dataProduct.DataProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class DataProductController {

    private final DataProductService dataProductService;
    //페이징 처리해서 데이터 상품 목록가져오기 (최신순으로)
    @GetMapping("/dataProducts")
    public CustomResponseEntity<Page<DataProductResponse.findDataProducts>> findDataProducts(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
    Pageable pageable)
    {
        return CustomResponseEntity.success(dataProductService.findDataProducts(pageable));
    }

    // 데이터 상품 생성(GANERTE 이용)
//    @PostMapping("/dataProduct")
//    public CustomResponseEntity<DataProductResponse.createDataProduct> createDataProduct(
//            @AuthenticationPrincipal final Long userId, @RequestPart final DataProductRequest.createDataProduct request, @RequestPart MultipartFile zipfile){
//        return CustomResponseEntity.success(dataProductService.createDataProduct(userId, request, zipfile));
//    }폼

    //데이터 상품 판매(그냥 폼을 이용해 올리기)


    // 단일 데이터 상품 조회
    @GetMapping("/dataProduct/{dataProductId}")
    public CustomResponseEntity<DataProductResponse.findDataProduct> findDataProduct(@PathVariable Long dataProductId){
        return CustomResponseEntity.success(dataProductService.findDataProduct(dataProductId));
    }
//
//    // 데이터 상품 구매(아님 Order에서 주문 주문내역 조회, 주문 취소 등을 개발?

    // 카테고리별 조회(카테고리 아이디 전달받아서 해당 id만 조회 페이징)

    // 가격별 조회

    // 상품명 조회

    //카테고리, 가격, 상품명

}
