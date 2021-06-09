package com.table.order.domain.store.entity;

import lombok.Getter;

@Getter
public enum StoreStatus {
    VALID("승인"), INVALID("미승인");

    private String message;

    StoreStatus(String message) {
        this.message = message;
    }
}
