package com.table.order.domain.table.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.table.order.domain.order.dto.OrderDto;
import com.table.order.domain.table.entity.TableStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TableDto {
    private Long id;
    private String name;
    private int numberOfPeople;
    private int totalPrice;
    private TableStatus tableStatus;
    private List<OrderDto> orders;

    @Builder
    @QueryProjection
    public TableDto(Long id, String name, int numberOfPeople, int totalPrice, TableStatus tableStatus, List<OrderDto> orders) {
        this.id = id;
        this.name = name;
        this.numberOfPeople = numberOfPeople;
        this.totalPrice = totalPrice;
        this.tableStatus = tableStatus;
        this.orders = orders;
    }
}
