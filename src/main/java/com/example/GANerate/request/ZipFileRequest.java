package com.example.GANerate.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ZipFileRequest {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ganerateZip{
        private String originalFileName;
        private String uploadUrl;
        private Long createDataSize;
        private String uploadFileName;
        private Long dataProductId;
    }
}
