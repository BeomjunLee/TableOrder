package com.table.order.domain.category.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestUpdateCategory {
    private String name;

    @Builder
    public RequestUpdateCategory(String name) {
        this.name = name;
    }
}
