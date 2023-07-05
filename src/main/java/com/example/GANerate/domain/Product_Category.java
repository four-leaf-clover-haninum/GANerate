package com.example.GANerate.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Product_Category {

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


    public void setDataProduct(DataProduct dataProduct){
        if(this.dataProduct != null){
            this.dataProduct.getProduct_categories().remove(this);
        }
        this.dataProduct=dataProduct;
        dataProduct.getProduct_categories().add(this);
    }

    public void setCategory(Category category){
        if(this.category != null){
            this.category.getProduct_categories().remove(this);
        }
        this.category=category;
        category.getProduct_categories().add(this);
    }
}
