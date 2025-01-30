package com.mapago.controller.portal.category;

import com.mapago.model.category.Category;
import com.mapago.service.category.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/portal/api/category")
public class PtlCategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public ResponseEntity<?> list(Category category) throws Exception {
        category.setNoCount(true);
        return ResponseEntity.ok(categoryService.getCategoryList(category));
    }

    @GetMapping("/tree")
    public ResponseEntity<?> tree(Category category) throws Exception {
        return ResponseEntity.ok(categoryService.getCategoryTree(category));
    }

}
