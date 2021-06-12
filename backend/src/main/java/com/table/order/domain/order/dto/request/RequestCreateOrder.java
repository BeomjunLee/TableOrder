package com.table.order.domain.order.dto.request;
import com.table.order.domain.item.dto.OrderItemDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestCreateOrder {
    private List<OrderItemDto> items;
}
