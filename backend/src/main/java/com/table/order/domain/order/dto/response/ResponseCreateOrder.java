package com.table.order.domain.order.dto.response;

import com.table.order.domain.order.dto.OrderDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResponseCreateOrder {
    private int status;
    private String message;
    private List<OrderDto> data;

    @Builder
    public ResponseCreateOrder(int status, String message, List<OrderDto> data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
