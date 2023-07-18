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
    public CustomResponseEntity<Page<DataProductResponse.findAllCreateAt>> findAll(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
    Pageable pageable)
    {
        return CustomResponseEntity.success(dataProductService.findAllDataProduct(pageable));
    }

    // 데이터 상품 생성
//    @PostMapping("/dataProduct")
//    public CustomResponseEntity<DataProductResponse.createProduct> createProduct(
//            @AuthenticationPrincipal final Long userId, @RequestPart final DataProductRequest.createProduct request, @RequestPart MultipartFile zipfile){
//        return CustomResponseEntity.success(dataProductService.createProduct(userId, request, zipfile));
//    }


//    // 단일 데이터 상품 조회
//    @GetMapping("/dataProduct/{dataProductId}")
//
//    // 데이터 상품 구매(아님 Order에서 주문 주문내역 조회, 주문 취소 등을 개발?

    // 카테고리별 조회(카테고리 아이디 전달받아서 해당 id만 조회 페이징)

    // 가격별 조회

    // 상품명 조회

    //카테고리, 가격, 상품명

}
