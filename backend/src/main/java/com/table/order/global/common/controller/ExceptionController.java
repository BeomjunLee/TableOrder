package com.table.order.global.common.controller;

import com.table.order.domain.store.exception.CustomAccessDeniedException;
import com.table.order.global.common.response.ResponseError;
import com.table.order.global.common.exception.CustomIllegalArgumentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ExceptionController {

    @ExceptionHandler(CustomIllegalArgumentException.class)
    public ResponseEntity illegalArgumentException(CustomIllegalArgumentException e) {
        log.error(e.getMessage());

        ResponseError response = ResponseError.builder()
                .errorCode(e.getErrorCode())
                .status(BAD_REQUEST.value())
                .message(e.getMessage())
                .build();
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(CustomAccessDeniedException.class)
    public ResponseEntity accessDenied(CustomAccessDeniedException e) {
        log.error(e.getMessage());

        ResponseError response = ResponseError.builder()
                .errorCode(e.getErrorCode())
                .status(FORBIDDEN.value())
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(FORBIDDEN).body(response);
    }
}
