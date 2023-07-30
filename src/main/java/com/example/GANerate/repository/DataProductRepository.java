package com.example.GANerate.repository;

import com.example.GANerate.domain.DataProduct;
import com.example.GANerate.domain.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataProductRepository extends JpaRepository<DataProduct, Long>{

    Page<DataProduct> findAllBy(Pageable pageable);

    Page<DataProduct> findAllByProductCategoriesIn(List<ProductCategory> productCategories, Pageable pageable);

    List<DataProduct> findTop3ByOrderByBuyCntDesc();

//    @Query("SELECT dp.id AS dataProductId, dp.buyCnt, dp.price, dp.title, dp.description, dp.imageUrl, dp.createdAt, c.id AS categoryId, c.title AS categoryName "
//            + "FROM DataProduct dp "
//            + "LEFT JOIN dp.product_categories pc "
//            + "LEFT JOIN pc.category c "
//            + "WHERE (:minprice IS NULL OR dp.price BETWEEN :minprice AND :maxprice) "
//            + "AND (:title IS NULL OR dp.title LIKE CONCAT('%', :title, '%')) "
//            + "AND (c.id IS NULL OR c.id IN :categoryids)")
//    Page<DataProductResponse.findDataProducts> findDataProductsFiltered(
//            @Param("title") String title, @Param("maxprice") Long maxPrice, @Param("minprice") Long minPrice, @Param("categoryids") List<Long>categoriesId, Pageable pageable);

}
