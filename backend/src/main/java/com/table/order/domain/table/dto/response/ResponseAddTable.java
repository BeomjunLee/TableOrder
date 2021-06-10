package com.table.order.domain.table.dto.response;

import com.table.order.domain.table.dto.TableDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResponseAddTable {
    private int status;
    private String message;
    private TableDto data;

    @Builder
    public ResponseAddTable(int status, String message, TableDto data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
