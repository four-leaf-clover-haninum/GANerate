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
public class DataProduct extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "data_product_id")
    private Long id;

    @NotNull
    private Long buyCnt;

    //원하는 데이터 수량(이미지가 몇개들어있는지)
    @NotNull
    private Long dataSize;

    @NotNull
    private String title;

    @NotNull
    private Long price;

    @NotNull
    private String description;

    private String imageUrl;

    @OneToMany(mappedBy = "dataProduct", cascade = CascadeType.ALL)
    private List<Product_Category> product_categories = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "zipfile_id")
    private ZipFile zipFile;

    @Builder
    public DataProduct(Long buyCnt, String title, Long price, String description, Long dataSize, String imageUrl, List<Product_Category> product_categories){
        this.buyCnt = buyCnt;
        this.title=title;
        this.price=price;
        this.description=description;
        this.dataSize=dataSize;
        this.imageUrl=imageUrl;
        this.product_categories=product_categories;
    }

    /**
     * 다운로드 증가
     */
    public void addDownloadCnt(){
        this.buyCnt +=1;
    }
}
