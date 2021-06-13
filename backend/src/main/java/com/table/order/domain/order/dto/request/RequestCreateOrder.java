package com.table.order.domain.order.dto.request;
import com.table.order.domain.item.dto.OrderItemDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestCreateOrder {
    private List<OrderItemDto> items;
    private String request;

    @Builder
    public RequestCreateOrder(List<OrderItemDto> items, String request) {
        this.items = items;
        this.request = request;
    }
}
