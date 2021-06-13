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

    @Builder
    public OrderItemDto(Long id, int count) {
        this.id = id;
        this.count = count;
    }
}
