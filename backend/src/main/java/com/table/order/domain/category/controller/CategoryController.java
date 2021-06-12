package com.table.order.domain.category.controller;

import com.table.order.domain.category.dto.request.RequestAddCategory;
import com.table.order.domain.category.dto.response.ResponseAddCategory;
import com.table.order.domain.category.dto.response.ResponseCategoriesItems;
import com.table.order.domain.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseCategoriesItems findCategoriesUser(Authentication authentication) {
        return categoryService.findCategoriesUser(authentication.getName());
    }

    @GetMapping("/app/categories/items")
    public ResponseCategoriesItems findCategoriesCustomer(Authentication authentication) {
        return categoryService.findCategoriesCustomer(authentication.getName());
    }
}
