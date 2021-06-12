package com.table.order.domain.order.controller;
import com.table.order.domain.order.dto.request.RequestCreateOrder;
import com.table.order.domain.order.dto.response.ResponseCreateOrder;
import com.table.order.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/app/orders")
    @ResponseStatus(CREATED)
    public ResponseCreateOrder createOrder(@RequestBody RequestCreateOrder requestCreateOrder,
                                           Authentication authentication) {
        return orderService.createOrder(requestCreateOrder, authentication.getName());
    }
}
