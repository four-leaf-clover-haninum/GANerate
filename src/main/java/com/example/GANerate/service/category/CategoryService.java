package com.example.GANerate.service.category;

import com.example.GANerate.domain.Category;
import com.example.GANerate.repository.CategoryRepository;
import com.example.GANerate.response.CustomResponseEntity;
import com.example.GANerate.response.category.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    // 전체 카테고리 조회
    public List<CategoryResponse.CategoryList> getCategories(){
        List<Category> categories = categoryRepository.findAll();
        List<CategoryResponse.CategoryList> categoryLists = new ArrayList<>();

        for (Category category : categories) {
            CategoryResponse.CategoryList dto = CategoryResponse.CategoryList.builder()
                    .categoryCode(category.getCategoryCode())
                    .title(category.getTitle())
                    .build();
            categoryLists.add(dto);
        }
        return categoryLists;
    }
}
