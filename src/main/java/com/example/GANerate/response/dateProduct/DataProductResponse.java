package com.example.GANerate.response.dateProduct;

import com.example.GANerate.enumuration.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class DataProductResponse {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class findDataProducts {
        private Long dataProductId;
        private Long buyCnt;
        private String title;
        private Long price;
        private String description;
        private String imageUrl;
        private LocalDateTime createdAt;
        private List<Long> categoryIds;
        private List<String> categoryNames;
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
        private Long dataProductId;
        private Long buyCnt;
        //원하는 데이터 수량(이미지가 몇개들어있는지)
        private Long dataSize;
        private String title;
        private Long price;
        private String description;
        private List<String> imageUrl; // 이거 이미지 예시 여러개 띄울려면 list로 받아야됨.
        private List<String> categoryNames;
        private List<Long> categoryIds;
        private String zipfileName;
        private double zipfileSize; //GB사이즈
        private LocalDateTime createdAt;
    }

    //데이터 상품 생성 폼 응답
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class saleDataProduct {
        private Long id;
    }

    // 데이터 zip 파일 업로드 응답
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class saleDataProductZip {
        private String zipFileUrl;
        private Long dataSize;
    }

    // 데이터 이미지 파일 업로드 응답
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class saleDataProductImages {
        private String imageUrl;
    }

//    @Data
//    @NoArgsConstructor
//    @AllArgsConstructor
//    @Builder
//    public static class findHeartDataProducts {
//        private Long dataProductId;
////        private Long buyCnt;
//        private String title;
//        private Long price;
//        private String description;
//        private String imageUrl;
//        private LocalDateTime createdAt;
//        private List<String> categoriesName;
//    }
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class orderDataProducts {
        private Long dataProductId;
        private Long orderId;
        private String title;
        private Long price;
        private String imageUrl;
        private LocalDateTime createdAt;
        private List<String> categoriesName;
        private OrderStatus orderStatus;
    }
}
