package com.example.GANerate.request.dateProduct;

import com.example.GANerate.domain.ProductCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class DataProductRequest {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class createProduct{
        private Long orderId;
        private String title;
        private String description;
//        private String imageUrl;
        private Long dataSize; // 원하는 이미지 수량
//        private LocalDateTime createdAt;
        private List<Long> categoryIds;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class filter{
        private List<Long> categoryIds;
        private String title;
        private Long minPrice;
        private Long maxPrice;
        private Integer page;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class saleProduct{
        @NotNull
        private String title;
        @NotNull
        private Long price;
        private Long dataSize;
        @NotNull
        private String description;
        @NotNull
        private List<Long> categoryIds;
        @NotNull
        private String zipFileUrl;
        @NotNull
        private List<String> imageUrls;
    }
}
