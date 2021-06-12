package com.table.order.domain.customer.controller;

import com.table.order.domain.customer.dto.request.RequestLoginCustomer;
import com.table.order.domain.customer.dto.response.ResponseLoginCustomer;
import com.table.order.domain.customer.service.CustomerService;
import com.table.order.global.common.code.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

        if(responseLoginCustomer.getStatus() == RESULT_RE_LOGIN.getStatus())
            return ResponseEntity.ok(responseLoginCustomer);

        return ResponseEntity.status(CREATED).body(responseLoginCustomer);
    }
}
