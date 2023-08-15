package com.example.GANerate.repository;

import com.example.GANerate.domain.DataProduct;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.function.Predicate;

public class DataProductSpecifications {
    public static Specification<DataProduct> hasTitle(String title) {
        return (root, query, criteriaBuilder) -> title != null ? criteriaBuilder.like(root.get("title"), "%" + title + "%") : null;
    }

    public static Specification<DataProduct> hasPriceBetween(Long minPrice, Long maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice != null && maxPrice != null) {
                return criteriaBuilder.between(root.get("price"), minPrice.doubleValue(), maxPrice.doubleValue());
            } else if (minPrice != null) {
                return criteriaBuilder.ge(root.get("price"), minPrice.doubleValue());
            } else if (maxPrice != null) {
                return criteriaBuilder.le(root.get("price"), maxPrice.doubleValue());
            }
            return null; // No condition to apply
        };
    }
}
