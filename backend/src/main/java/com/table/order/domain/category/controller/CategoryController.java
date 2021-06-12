package com.table.order.domain.category.controller;

import com.table.order.domain.category.dto.request.RequestAddCategory;
import com.table.order.domain.category.dto.response.ResponseAddCategory;
import com.table.order.domain.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("")
    @ResponseStatus(CREATED)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseAddCategory addCategory(@RequestBody RequestAddCategory requestAddCategory,
                                           Authentication authentication) {
        return categoryService.addCategory(requestAddCategory, authentication.getName());
    }
}
