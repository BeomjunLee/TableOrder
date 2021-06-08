package com.table.order.global.common;

import com.table.order.domain.customer.dto.response.ResponseLogin;
import com.table.order.domain.customer.exception.AlreadyInUseService;
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

    @ExceptionHandler(AlreadyInUseService.class)
    public ResponseEntity ticketDuplicated(AlreadyInUseService e) {
        log.error(e.getMessage());
        ResponseLogin response = ResponseLogin.builder()
                .status(HttpStatus.OK.value())
                .message(e.getMessage())
                .accessToken("") //TODO
                .expiredAt(1000) //TODO
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
