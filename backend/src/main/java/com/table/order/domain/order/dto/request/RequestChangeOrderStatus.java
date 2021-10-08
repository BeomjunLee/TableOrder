package com.table.order.domain.order.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestChangeOrderStatus {
    private List<OrderIdDto> ids;

    @Builder
    public RequestChangeOrderStatus(List<OrderIdDto> ids) {
        this.ids = ids;
    }
}
