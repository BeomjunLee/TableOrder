package com.table.order.global.common;

import com.table.order.global.common.exception.CustomIllegalArgumentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ExceptionController {

    @ExceptionHandler(CustomIllegalArgumentException.class)
    public ResponseEntity illegalArgumentException(CustomIllegalArgumentException e) {
        log.error(e.getMessage());

        ResponseError response = ResponseError.builder()
                .errorCode(e.getErrorCode())
                .status(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .build();
        return ResponseEntity.badRequest().body(response);
    }
}
