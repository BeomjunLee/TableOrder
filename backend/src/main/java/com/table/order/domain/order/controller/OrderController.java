package com.table.order.domain.order.controller;
import com.table.order.domain.order.dto.request.RequestChangeOrderStatus;
import com.table.order.domain.order.dto.request.RequestCreateOrder;
import com.table.order.domain.order.dto.response.ResponseCreateOrder;
import com.table.order.domain.order.service.OrderService;
import com.table.order.global.common.response.ResponseResult;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PostMapping("/app/orders/{orderId}/cancel")
    public ResponseResult cancelOrderCustomer(@PathVariable Long orderId,
                                              Authentication authentication) {
        return orderService.cancelOrderCustomer(orderId, authentication.getName());
    }

    @PostMapping("/orders/{orderId}/cancel")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseResult cancelOrderUser(@PathVariable Long orderId,
                                          Authentication authentication) {
        return orderService.cancelOrderUser(orderId, authentication.getName());
    }

    @PostMapping("/orders/cook")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseResult changeOrderStatusCooked(@RequestBody RequestChangeOrderStatus requestChangeOrderStatus,
                                                  Authentication authentication) {
        return orderService.changeOrderStatusCooked(requestChangeOrderStatus, authentication.getName());
    }

    @PostMapping("/orders/cook_comp")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseResult changeOrderStatusCookComp(@RequestBody RequestChangeOrderStatus requestChangeOrderStatus,
                                                  Authentication authentication) {
        return orderService.changeOrderStatusCookComp(requestChangeOrderStatus, authentication.getName());
    }

    @PostMapping("/orders/comp")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseResult changeOrderStatusComp(@RequestBody RequestChangeOrderStatus requestChangeOrderStatus,
                                                  Authentication authentication) {
        return orderService.changeOrderStatusComp(requestChangeOrderStatus, authentication.getName());
    }
}
