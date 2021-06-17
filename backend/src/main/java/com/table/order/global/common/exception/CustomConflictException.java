package com.table.order.global.common.exception;

import lombok.Getter;

@Getter
public class CustomConflictException extends RuntimeException{
    private final int errorCode;

    public CustomConflictException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
