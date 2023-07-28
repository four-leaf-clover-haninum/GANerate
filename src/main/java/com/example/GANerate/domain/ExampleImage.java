package com.example.GANerate.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExampleImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "example_image_id")
    private Long id;

    @NotNull
    private String originalFileName;

    @NotNull
    private String uploadFileName;
    @Column(length = 15000)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_product_id")
    private DataProduct dataProduct;

    @Builder
    public ExampleImage(String originalFileName, String uploadFileName, String imageUrl){
        this.originalFileName = originalFileName;
        this.uploadFileName = uploadFileName;
        this.imageUrl = imageUrl;
    }

    public void setDataProduct(DataProduct dataProduct){
        this.dataProduct=dataProduct;
        dataProduct.getExampleImages().add(this);
    }
}
