package com.example.GANerate.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DataProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "data_product_id")
    private Long id;

    @NotNull
    private Long downloadCnt;

    @NotNull
    private String title;

    @NotNull
    private Long price;

    @NotNull
    private String description;

    @OneToMany(mappedBy = "dataProduct", cascade = CascadeType.ALL)
    private List<Product_Category> product_categories = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "zipfile_id")
    private ZipFile zipFile;

    @Builder
    public DataProduct(Long downloadCnt, String title, Long price, String description){
        this.downloadCnt=downloadCnt;
        this.title=title;
        this.price=price;
        this.description=description;
    }
}
