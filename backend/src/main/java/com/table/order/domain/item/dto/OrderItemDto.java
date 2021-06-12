package com.table.order.domain.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItemDto {
    private Long id;
    private int count;
    private String request;

    @Builder
    public OrderItemDto(Long id, int count, String request) {
        this.id = id;
        this.count = count;
        this.request = request;
    }
}
