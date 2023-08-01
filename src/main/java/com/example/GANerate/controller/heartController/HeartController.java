package com.example.GANerate.controller.heartController;

import com.example.GANerate.response.CustomResponseEntity;
import com.example.GANerate.response.heart.HeartResponse;
import com.example.GANerate.service.heart.HeartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HeartController {

    private final HeartService heartService;

    // 좋아요 누르기
    @PostMapping("/v1/hearts/{data-product-id}")
    public CustomResponseEntity<HeartResponse> like(@PathVariable("data-product-id") Long dataProductId) {
        return CustomResponseEntity.success(heartService.like(dataProductId));
    }

//    @PostMapping("/v1/hearts/{data-product-id}")
//    public CustomResponseEntity<HeartResponse> like(@AuthenticationPrincipal userId, @PathVariable("data-product-id") Long dataProductId) {
//        return CustomResponseEntity.success(heartService.like(dataProductId));
//    }

    // 좋아요 취소
    @DeleteMapping("/v1/hearts/{data-product-id}")
    public CustomResponseEntity unlike(@PathVariable("data-product-id") Long dataProductId){
        heartService.unlike(dataProductId);
        return CustomResponseEntity.success();
    }

}
