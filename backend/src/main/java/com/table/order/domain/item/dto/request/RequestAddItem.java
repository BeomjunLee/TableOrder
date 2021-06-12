package com.table.order.domain.item.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestAddItem {
    private String name;
    private String description;
    private int price;
    private String image;

    private Long categoryId;

    @Builder
    public RequestAddItem(String name, String description, int price, String image, Long categoryId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.categoryId = categoryId;
    }
}
