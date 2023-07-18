package com.example.GANerate.response.dateProduct;

import com.example.GANerate.domain.Category;
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
    public static class findAllCreateAt{
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
    public static class createProduct {
        private String title;
        private Long price;
        private String description;
        private String imageUrl;
    }
}
