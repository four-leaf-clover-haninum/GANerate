package com.example.GANerate.response.dateProduct;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class DataProductResponse {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class findDataProducts {
        private Long id;
        private Long buyCnt;
        private String title;
        private Long price;
        private String description;
        private String imageUrl;
        private LocalDateTime createdAt;
        private List<Long> categoryId;
        private List<String> categoriesName;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class createDataProduct {
        private String title;
        private Long price;
        private String description;
        private String imageUrl;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class findDataProduct {
        private Long id;
        private Long buyCnt;
        //원하는 데이터 수량(이미지가 몇개들어있는지)
        private Long dataSize;
        private String title;
        private Long price;
        private String description;
        private List<String> imageUrl; // 이거 이미지 예시 여러개 띄울려면 list로 받아야됨.
        private List<String> categoriseName;
        private String zipfileName;
        private int zipfileSize;
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class saleDataProduct {
        private Long id;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class findHeartDataProducts {
        private Long productId;
//        private Long buyCnt;
        private String title;
        private Long price;
        private String description;
        private String imageUrl;
        private LocalDateTime createdAt;
        private List<String> categoriesName;
    }
}
