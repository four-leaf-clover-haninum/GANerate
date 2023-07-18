package com.example.GANerate.controller.categoryController;

import com.example.GANerate.response.CustomResponseEntity;
import com.example.GANerate.response.category.CategoryResponse;
import com.example.GANerate.service.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private CategoryService categoryService;

    //전체 카테고리 조회
    @GetMapping("/categories")
    public CustomResponseEntity<List<CategoryResponse.CategoryList>> getCategories(){
        return CustomResponseEntity.success(categoryService.getCategories());
    }
}
