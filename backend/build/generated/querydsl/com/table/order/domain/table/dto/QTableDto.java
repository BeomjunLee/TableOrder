package com.table.order.domain.table.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.Generated;

/**
 * com.table.order.domain.table.dto.QTableDto is a Querydsl Projection type for TableDto
 */
@Generated("com.querydsl.codegen.ProjectionSerializer")
public class QTableDto extends ConstructorExpression<TableDto> {

    private static final long serialVersionUID = -1496954211L;

    public QTableDto(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<String> name, com.querydsl.core.types.Expression<Integer> numberOfPeople, com.querydsl.core.types.Expression<Integer> totalPrice, com.querydsl.core.types.Expression<com.table.order.domain.table.entity.TableStatus> tableStatus, com.querydsl.core.types.Expression<? extends java.util.List<com.table.order.domain.order.dto.OrderDto>> orders) {
        super(TableDto.class, new Class<?>[]{long.class, String.class, int.class, int.class, com.table.order.domain.table.entity.TableStatus.class, java.util.List.class}, id, name, numberOfPeople, totalPrice, tableStatus, orders);
    }

}

