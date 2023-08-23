package com.example.GANerate.service.category;

import com.example.GANerate.config.timer.Timer;
import com.example.GANerate.domain.Category;
import com.example.GANerate.repository.CategoryRepository;
import com.example.GANerate.response.category.CategoryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    private final CategoryRepository categoryRepository;

    // 전체 카테고리 조회
    @Transactional(readOnly = true)
    @Timer
    public List<CategoryResponse.findCategories> findCategories(){
        List<Category> categories = categoryRepository.findAll();

        return categories.stream()
                .map(category -> new CategoryResponse.findCategories(category.getId(), category.getCategoryCode(), category.getTitle())).collect(Collectors.toList());
    }
}
