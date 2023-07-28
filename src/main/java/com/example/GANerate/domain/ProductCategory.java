package com.example.GANerate.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_category_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_product_id")
    private DataProduct dataProduct;

    @Builder
    public ProductCategory(Category category, DataProduct dataProduct){
        this.category=category;
        this.dataProduct=dataProduct;
    }


    public void setDataProduct(DataProduct dataProduct){
        this.dataProduct=dataProduct;
    }

    public void setCategory(Category category){
        this.category=category;
    }
}
