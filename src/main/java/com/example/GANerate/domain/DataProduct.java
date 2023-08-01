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

//    @NotNull
//    private String ThumbnailImage;

    @NotNull
    private String title;

    @NotNull
    private Long price;

    @NotNull
    private String description;

    @OneToMany(mappedBy = "dataProduct", cascade = CascadeType.ALL)
    private List<ProductCategory> productCategories = new ArrayList<>();

    @OneToMany(mappedBy = "dataProduct", cascade = CascadeType.ALL)
    private List<ExampleImage> exampleImages = new ArrayList<>();



    @OneToOne
    @JoinColumn(name = "zipfile_id")
    private ZipFile zipFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public DataProduct(Long id, Long buyCnt, String title, Long price, String description, Long dataSize){
        this.id = id;
        this.buyCnt = buyCnt;
        this.title=title;
        this.price=price;
        this.description=description;
        this.dataSize=dataSize;

    }

    /**
     * 다운로드 증가
     */
    public void addDownloadCnt(){
        this.buyCnt +=1;
    }

    public void setZipFile(ZipFile zipFile){
        this.zipFile=zipFile;
    }
    public void setUser(User user){
        this.user = user;
        user.getDataProducts().add(this);
    }

    public void addProductCategory(ProductCategory productCategory){
        this.productCategories.add(productCategory);
        productCategory.setDataProduct(this);
    }
}
