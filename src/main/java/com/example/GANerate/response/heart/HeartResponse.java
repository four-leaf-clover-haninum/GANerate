package com.example.GANerate.response.heart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class HeartResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class likeResponse {
        private Long heartId;
    }

}
