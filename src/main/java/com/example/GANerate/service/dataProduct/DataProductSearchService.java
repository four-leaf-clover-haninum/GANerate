package com.example.GANerate.service.dataProduct;

import com.example.GANerate.config.timer.Timer;
import com.example.GANerate.domain.DataProduct;
import com.example.GANerate.domain.ProductCategory;
import com.example.GANerate.enumuration.Result;
import com.example.GANerate.exception.CustomException;
import com.example.GANerate.repository.DataProductRepository;
import com.example.GANerate.repository.DataProductSpecifications;
import com.example.GANerate.request.dateProduct.DataProductRequest;
import com.example.GANerate.response.dateProduct.DataProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataProductSearchService {

    private final DataProductRepository dataProductRepository;

    // 조건 검색 Specification
    @Transactional(readOnly = true)
    @Timer
    public Page<DataProductResponse.findDataProducts> findDataProductsFiltered(DataProductRequest.filter request) {
        try {
            Pageable pageable = PageRequest.of(request.getPage(), 10, Sort.by("createdAt").descending());
            String title = request.getTitle();
            Long maxPrice = request.getMaxPrice();
            Long minPrice = request.getMinPrice();
            List<Long> categoriesId = request.getCategoryIds();

            Specification<DataProduct> titleSpec = DataProductSpecifications.hasTitle(title);
            Specification<DataProduct> priceSpec = DataProductSpecifications.hasPriceBetween(minPrice, maxPrice);

            List<DataProductResponse.findDataProducts> findDataProducts = new ArrayList<>();
            List<DataProduct> findFilteredAll = dataProductRepository.findAll(Specification.where(titleSpec).and(priceSpec));
            for (DataProduct dataProduct : findFilteredAll) {
                List<ProductCategory> productCategories = dataProduct.getProductCategories();
                List<Long> categoryIds = new ArrayList<>();
                List<String> categoryNames = new ArrayList<>();
                for (ProductCategory productCategory : productCategories) {
                    Long categoryId = productCategory.getCategory().getId();
                    String categoryName = productCategory.getCategory().getTitle();
                    categoryIds.add(categoryId);
                    categoryNames.add(categoryName);
                }
                if (categoriesId.isEmpty() || categoryIds.containsAll(categoriesId)) {
                    DataProductResponse.findDataProducts response = DataProductResponse.findDataProducts.builder()
                            .dataProductId(dataProduct.getId())
                            .buyCnt(dataProduct.getBuyCnt())
                            .title(dataProduct.getTitle())
                            .price(dataProduct.getPrice())
                            .description(dataProduct.getDescription())
                            .imageUrl(dataProduct.getExampleImages().get(0).getImageUrl())
                            .createdAt(dataProduct.getCreatedAt())
                            .categoryIds(categoryIds)
                            .categoryNames(categoryNames)
                            .build();
                    findDataProducts.add(response);
                }
            }
            return new PageImpl<>(findDataProducts, pageable, findFilteredAll.size());
        }catch (Exception e) {
            throw new CustomException(Result.NOT_FOUND_DATA_PRODUCT);
        }
    }
}
