package com.table.order.global.common.exception;

import lombok.Getter;

@Getter
public class CustomIllegalArgumentException extends RuntimeException{

    private final int errorCode;

    public CustomIllegalArgumentException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
