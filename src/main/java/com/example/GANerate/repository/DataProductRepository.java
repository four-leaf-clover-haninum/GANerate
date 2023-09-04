package com.example.GANerate.repository;

import com.example.GANerate.domain.DataProduct;
import com.example.GANerate.domain.DataProductType;
import com.example.GANerate.domain.ProductCategory;
import com.example.GANerate.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataProductRepository extends JpaRepository<DataProduct, Long>, JpaSpecificationExecutor<DataProduct> {

    Page<DataProduct> findAllByDataProductType(Pageable pageable, DataProductType dataProductType);

    Page<DataProduct> findAllByProductCategoriesInAndDataProductType(List<ProductCategory> productCategories, DataProductType dataProductType, Pageable pageable);

    List<DataProduct> findTop3ByOrderByBuyCntDesc();

    // 회원이 판매하는 데이터 상품 조회
    List<DataProduct> findByUser(User user);
}
