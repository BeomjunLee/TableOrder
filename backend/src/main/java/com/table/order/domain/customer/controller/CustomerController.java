package com.table.order.domain.customer.controller;

import com.table.order.domain.customer.dto.request.RequestLoginCustomer;
import com.table.order.domain.customer.dto.response.ResponseLoginCustomer;
import com.table.order.domain.customer.service.CustomerService;
import com.table.order.global.common.code.ResultCode;
import com.table.order.global.common.response.ResponseResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.table.order.global.common.code.ResultCode.RESULT_RE_LOGIN;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/app/customers")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("")
    public ResponseEntity<ResponseLoginCustomer> scanQrCode(@RequestBody RequestLoginCustomer requestLoginCustomer) {
        ResponseLoginCustomer responseLoginCustomer = customerService.scanQrCode(requestLoginCustomer);

        if (responseLoginCustomer.getStatus() == RESULT_RE_LOGIN.getStatus())
            return ResponseEntity.ok(responseLoginCustomer);

        return ResponseEntity.status(CREATED).body(responseLoginCustomer);
    }

    @GetMapping("/test")
    public ResponseResult test() {
        return ResponseResult.builder()
                .status(200)
                .message("테스트 성공")
                .build();
    }
}
