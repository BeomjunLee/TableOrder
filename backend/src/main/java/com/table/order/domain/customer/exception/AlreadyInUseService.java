package com.table.order.domain.customer.exception;

public class AlreadyInUseService extends RuntimeException {

    public AlreadyInUseService(String message) {
        super(message);
    }
}
