package com.table.order.domain.category.controller;
import com.table.order.domain.category.dto.request.RequestAddCategory;
import com.table.order.domain.category.dto.request.RequestUpdateCategory;
import com.table.order.domain.category.dto.response.ResponseAddCategory;
import com.table.order.domain.category.dto.response.ResponseCategoriesItems;
import com.table.order.domain.category.service.CategoryService;
import com.table.order.global.common.response.ResponseResult;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/categories")
    @ResponseStatus(CREATED)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseAddCategory addCategory(@RequestBody RequestAddCategory requestAddCategory,
                                           Authentication authentication) {
        return categoryService.addCategory(requestAddCategory, authentication.getName());
    }

    @GetMapping("/categories/items")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseCategoriesItems findCategoriesItemsUser(Authentication authentication) {
        return categoryService.findCategoriesUser(authentication.getName());
    }

    @GetMapping("/app/categories/items")
    public ResponseCategoriesItems findCategoriesItemsCustomer(Authentication authentication) {
        return categoryService.findCategoriesCustomer(authentication.getName());
    }

    @DeleteMapping("/categories/{categoryId}")
    public ResponseResult deleteCategory(@PathVariable Long categoryId,
                                         Authentication authentication) {
        return categoryService.deleteCategory(categoryId, authentication.getName());
    }

    @PutMapping("/categories/{categoryId}")
    public ResponseResult updateCategory(@PathVariable Long categoryId,
                                         @RequestBody RequestUpdateCategory requestUpdateCategory,
                                         Authentication authentication) {
        return categoryService.updateCategory(categoryId, authentication.getName(), requestUpdateCategory);
    }
}
