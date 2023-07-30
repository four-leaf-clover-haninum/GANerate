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
public class  Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    private int categoryCode;

    @NotNull
    private String title;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<ProductCategory> product_categories = new ArrayList<>();

    @Builder
    public Category(Long id, int categoryCode, String title){
        this.id=id;
        this.categoryCode = categoryCode;
        this.title=title;
    }

    public void addProduct_Category(ProductCategory product_category){
        product_categories.add(product_category);
        product_category.setCategory(this);
    }
}
