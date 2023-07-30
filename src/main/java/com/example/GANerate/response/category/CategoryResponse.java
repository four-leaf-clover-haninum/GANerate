package com.example.GANerate.response.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CategoryResponse {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class findCategories {

        private Long categoryId;
        private int categoryCode;
        private String title;

    }
}
