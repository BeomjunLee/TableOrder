package com.table.order.domain.customer.dto.request;

import lombok.Getter;

@Getter
public class RequestCustomerLogin {

    private String username;
    private Long storeId;
    private Long tableId;

}
