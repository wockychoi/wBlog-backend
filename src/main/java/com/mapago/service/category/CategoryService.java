package com.mapago.service.category;

import com.mapago.config.exception.CustomException;
import com.mapago.mapper.category.CategoryMapper;
import com.mapago.model.category.Category;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    public List<Category> getCategoryList(Category category) throws Exception {
        return categoryMapper.findCategoryList(category);
    }

    @Transactional
    public Category insertCategory(Category category) throws Exception {
        categoryMapper.insertCategory(category);
        return category;
    }

    @Transactional
    public Category updateCategory(Category category) throws Exception {
        int result = categoryMapper.updateCategory(category);
        return Optional.ofNullable(category)
                .filter(t -> result > 0)
                .orElseThrow(() -> new CustomException("수정할 데이터가 없습니다."));
    }

    @Transactional
    public Integer deleteCategory(Category category) throws Exception {
        return Optional.of(categoryMapper.deleteCategory(category))
                .filter(result -> result > 0)
                .orElseThrow(() -> new CustomException("삭제할 데이터가 없습니다."));
    }

    @Transactional
    public Integer sortCategoryList(List<Category> categoryList) throws Exception {
        int totalUpdated = categoryList.stream()
                .mapToInt(categoryMapper::sortCategory)
                .sum();

        if (totalUpdated <= 0) {
            throw new CustomException("정렬할 데이터가 없습니다.");
        }
        return totalUpdated;
    }

    public List<Category> getCategoryTree(Category category) throws Exception {
        return categoryMapper.findCategoryTree(category);
    }
}