package com.table.order.domain.category.dto.response;

import com.table.order.domain.category.dto.CategoryDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResponseAddCategory {
    private int status;
    private String message;
    private CategoryDto data;

    @Builder
    public ResponseAddCategory(int status, String message, CategoryDto data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
