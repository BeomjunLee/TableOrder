package com.table.order.domain.store.exception;

import lombok.Getter;

@Getter
public class CustomAccessDeniedException extends RuntimeException{

    private final int errorCode;

    public CustomAccessDeniedException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
