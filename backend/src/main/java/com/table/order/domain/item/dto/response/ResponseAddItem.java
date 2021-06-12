package com.table.order.domain.item.dto.response;

import com.table.order.domain.item.dto.ItemDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResponseAddItem {
    private int status;
    private String message;
    private ItemDto data;

    @Builder
    public ResponseAddItem(int status, String message, ItemDto data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
