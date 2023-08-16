package com.example.GANerate.service.order;

import com.example.GANerate.config.SecurityUtils;
import com.example.GANerate.domain.*;
import com.example.GANerate.enumuration.OrderStatus;
import com.example.GANerate.enumuration.Result;
import com.example.GANerate.exception.CustomException;
import com.example.GANerate.repository.DataProductRepository;
import com.example.GANerate.repository.OrderItemRepository;
import com.example.GANerate.repository.OrderRepository;
import com.example.GANerate.repository.UserRepository;
import com.example.GANerate.response.order.OrderResponse;
import com.example.GANerate.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserRepository userRepository;
    private final DataProductRepository dataProductRepository;
    private final UserService userService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional(readOnly = true)
    public OrderResponse.ProductBuyOrder productOrder(Long dataProductId){
        User user = userService.getCurrentUser();
        DataProduct dataProduct = dataProductRepository.findById(dataProductId).orElseThrow(() -> new CustomException(Result.NOT_FOUND_DATA_PRODUCT));
        ZipFile zipFile = dataProduct.getZipFile();

        List<String> categoryNames = new ArrayList<>();
        List<Long> categoryIds = new ArrayList<>();
        List<ProductCategory> productCategories = dataProduct.getProductCategories();
        for (ProductCategory productCategory : productCategories) {
            Category category = productCategory.getCategory();
            categoryNames.add(category.getTitle());
            categoryIds.add(category.getId());
        }

        return OrderResponse.ProductBuyOrder.builder()
                .userId(user.getId())
                .userName(user.getName())
                .userEmail(user.getEmail())
                .dataProductId(dataProduct.getId())
                .dataProductTitle(dataProduct.getTitle())
                .dataProductPrice(dataProduct.getPrice())
                .categoryNames(categoryNames)
                .categoryIds(categoryIds)
                .zipFileOriginalFileName(zipFile.getOriginalFileName())
                .zipFileSizeGb(zipFile.getSizeGb())
                .dataProductSize(dataProduct.getDataSize())
                .build();

    }

    @Transactional
    public OrderResponse.CreateOrder createOrder(Long dataProductId) {
        User user = userService.getCurrentUser();
        DataProduct dataProduct = dataProductRepository.findById(dataProductId).orElseThrow(() -> new CustomException(Result.NOT_FOUND_DATA_PRODUCT));

        Order order = Order.builder()
                .orderStatus(OrderStatus.ORDER).build();
        orderRepository.save(order);

        order.setUser(user);

        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .dataProduct(dataProduct)
                .build();
        orderItemRepository.save(orderItem);
        orderItem.setOrder(order);
        orderItem.setDataProduct(dataProduct);


        return OrderResponse.CreateOrder.builder()
                .orderId(order.getId())
                .userName(user.getName())
                .email(user.getEmail())
                .phoneNum(user.getPhoneNum())
                .price(dataProduct.getPrice())
                .dataProductName(dataProduct.getTitle())
                .dataProductId(dataProductId)
                .build();
    }
}
