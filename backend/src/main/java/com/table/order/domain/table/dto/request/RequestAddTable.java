package com.table.order.domain.table.dto.request;

import com.table.order.domain.table.entity.TableStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestAddTable {
    private String name;
    private int numberOfPeople;

    @Builder
    public RequestAddTable(String name, int numberOfPeople) {
        this.name = name;
        this.numberOfPeople = numberOfPeople;
    }
}
