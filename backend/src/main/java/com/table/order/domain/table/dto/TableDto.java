package com.table.order.domain.table.dto;

import com.table.order.domain.table.entity.TableStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TableDto {
    private Long id;
    private String name;
    private int numberOfPeople;
    private TableStatus tableStatus;

    @Builder
    public TableDto(Long id, String name, int numberOfPeople, TableStatus tableStatus) {
        this.id = id;
        this.name = name;
        this.numberOfPeople = numberOfPeople;
        this.tableStatus = tableStatus;
    }
}
