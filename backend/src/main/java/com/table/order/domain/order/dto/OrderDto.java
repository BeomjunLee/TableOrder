package com.table.order.domain.order.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.table.order.domain.order.entity.OrderStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderDto {
    private Long id;
    private String name;
    private int orderPrice;
    private int count;
    private String request;
    private OrderStatus orderStatus;

    @Builder
    @QueryProjection
    public OrderDto(Long id, String name, int orderPrice, int count, String request, OrderStatus orderStatus) {
        this.id = id;
        this.name = name;
        this.orderPrice = orderPrice;
        this.count = count;
        this.request = request;
        this.orderStatus = orderStatus;
    }
}
