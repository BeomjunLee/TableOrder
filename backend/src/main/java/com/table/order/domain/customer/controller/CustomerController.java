package com.table.order.domain.customer.controller;

import com.table.order.domain.customer.dto.request.RequestCustomerLogin;
import com.table.order.domain.customer.dto.response.ResponseLogin;
import com.table.order.domain.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("")
    public ResponseLogin scanQrCode(@RequestBody RequestCustomerLogin requestCustomerLogin) {
        return customerService.scanQrCode(requestCustomerLogin);
    }
}
