package com.example.GANerate.repository;

import com.example.GANerate.domain.Category;
import com.example.GANerate.domain.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    List<ProductCategory> findAllByCategory(Category category);
}
