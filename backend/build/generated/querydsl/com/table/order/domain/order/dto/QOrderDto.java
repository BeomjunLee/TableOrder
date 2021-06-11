package com.table.order.domain.order.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.Generated;

/**
 * com.table.order.domain.order.dto.QOrderDto is a Querydsl Projection type for OrderDto
 */
@Generated("com.querydsl.codegen.ProjectionSerializer")
public class QOrderDto extends ConstructorExpression<OrderDto> {

    private static final long serialVersionUID = 1888269213L;

    public QOrderDto(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<Integer> orderPrice, com.querydsl.core.types.Expression<Integer> count, com.querydsl.core.types.Expression<String> request, com.querydsl.core.types.Expression<com.table.order.domain.order.entity.OrderStatus> orderStatus) {
        super(OrderDto.class, new Class<?>[]{long.class, int.class, int.class, String.class, com.table.order.domain.order.entity.OrderStatus.class}, id, orderPrice, count, request, orderStatus);
    }

}

