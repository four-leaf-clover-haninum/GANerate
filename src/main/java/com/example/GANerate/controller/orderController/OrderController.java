package com.example.GANerate.controller.orderController;

import com.example.GANerate.response.CustomResponseEntity;
import com.example.GANerate.response.order.OrderResponse;
import com.example.GANerate.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    //주문은 두가지 경우가 있음, 올라와있는 데이터 구매의 경우와 데이터 생성 요청


    // 주문 확인창
    @GetMapping("/v1/orders/{data-product-id}")
    public CustomResponseEntity<OrderResponse.ProductBuyOrder> productOrder(@PathVariable("data-product-id") Long dataProductId){
        return CustomResponseEntity.success(orderService.productOrder(dataProductId));
    }

    // 데이터 결제완료시 주문을 생성함
//    @PostMapping("/v1/orders/{data-product-id}")
//    public CustomResponseEntity<OrderResponse.CreateOrder> createOrder(@PathVariable("data-product-id") Long dataProductId){
//        return CustomResponseEntity.success(orderService.createOrder(dataProductId));
//    }
}
