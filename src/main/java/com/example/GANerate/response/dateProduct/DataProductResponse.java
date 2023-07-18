package com.example.GANerate.response.dateProduct;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class DataProductResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class findDataProducts {
        private Long id;
        private Long downloadCnt;
        private String title;
        private Long price;
        private String description;
        private String imageUrl;
        private LocalDateTime createdAt;
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
        private String imageUrl; // 이거 이미지 예시 여러개 띄울려면 list로 받아야됨.
        private List<String> categoriseName;
        private String zipfileName;
        private int zipfileSize;
        private LocalDateTime createdAt;
    }
}
