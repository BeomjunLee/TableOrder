package com.table.order.domain.order.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderIdDto {
    private Long id;

    @Builder
    public OrderIdDto(Long id) {
        this.id = id;
    }
}
