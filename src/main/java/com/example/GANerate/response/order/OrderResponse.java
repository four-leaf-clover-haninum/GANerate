package com.example.GANerate.response.order;

import com.example.GANerate.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class OrderResponse {


    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductBuyOrder{
        private Long userId;
        private String userName;
        private String userEmail;
        private Long dataProductId;
        private String dataProductTitle;
        private Long dataProductPrice;
        private List<String> categoryNames;
        private List<Long> categoryIds;
        private String zipFileOriginalFileName;
        private double zipFileSizeGb;
        private Long dataProductSize;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateOrder{
        private String dataProductName;
        private Long dataProductId;
        private Long price;
        private String email;
        private String userName;
        private String phoneNum;
        private Long orderId;
    }


}
