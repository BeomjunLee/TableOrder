package com.table.order.domain.customer.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestCustomerLogin {

    private String username;
    private Long storeId;
    private Long tableId;

    @Builder
    public RequestCustomerLogin(String username, Long storeId, Long tableId) {
        this.username = username;
        this.storeId = storeId;
        this.tableId = tableId;
    }
}
