package com.mapago.controller.admin.category;

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
@RequestMapping("/admin/api/category")
public class AdmCategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public ResponseEntity<?> list(Category category) throws Exception {
        return ResponseEntity.ok(categoryService.getCategoryList(category));
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody Category category) throws Exception {
        return ResponseEntity.ok(categoryService.insertCategory(category));
    }

    @PostMapping("/modify")
    public ResponseEntity<?> modify(@RequestBody Category category) throws Exception {
        return ResponseEntity.ok(categoryService.updateCategory(category));
    }

    @PostMapping("/remove")
    public ResponseEntity<?> remove(@RequestBody Category category) throws Exception {
        return ResponseEntity.ok(categoryService.deleteCategory(category));
    }

    @PostMapping("/sort")
    public ResponseEntity<?> sort(@RequestBody List<Category> categoryList) throws Exception {
        return ResponseEntity.ok(categoryService.sortCategoryList(categoryList));
    }

}
