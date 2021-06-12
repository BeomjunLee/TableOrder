package com.table.order.domain.category.dto.response;

import com.table.order.domain.category.dto.CategoryDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResponseCategoriesItems {
    private int status;
    private String message;
    private List<CategoryDto> data;

    @Builder
    public ResponseCategoriesItems(int status, String message, List<CategoryDto> data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
