package com.table.order.domain.table.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestUpdateTable {
    private String name;
    private int numberOfPeople;

    @Builder
    public RequestUpdateTable(String name, int numberOfPeople) {
        this.name = name;
        this.numberOfPeople = numberOfPeople;
    }
}
