package com.table.order.domain.order.service;
import com.table.order.domain.customer.entity.Customer;
import com.table.order.domain.customer.repository.CustomerQueryRepository;
import com.table.order.domain.item.dto.OrderItemDto;
import com.table.order.domain.item.entity.Item;
import com.table.order.domain.item.repository.ItemQueryRepository;
import com.table.order.domain.order.dto.OrderDto;
import com.table.order.domain.order.dto.request.RequestCreateOrder;
import com.table.order.domain.order.dto.response.ResponseCreateOrder;
import com.table.order.domain.order.entity.Order;
import com.table.order.domain.order.repository.OrderRepository;
import com.table.order.global.common.code.CustomErrorCode;
import com.table.order.global.common.exception.CustomIllegalArgumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.table.order.global.common.code.CustomErrorCode.ERROR_NOT_FOUND_USER_TABLE;
import static com.table.order.global.common.code.ResultCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemQueryRepository itemQueryRepository;
    private final CustomerQueryRepository customerQueryRepository;

    /**
     * 메뉴 주문 (장바구니에 담았다가 한번에 주문)
     * @param requestCreateOrder 주문 요청 dto (여러가지 메뉴들이 담겨있음)
     * @param username 손님 아이디
     * @return 응답 dto
     */
    public ResponseCreateOrder createOrder(RequestCreateOrder requestCreateOrder, String username) {
        Customer findCustomer = customerQueryRepository.findByUsernameJoinTable(username)
                .orElseThrow(() -> new CustomIllegalArgumentException(ERROR_NOT_FOUND_USER_TABLE.getErrorCode(), ERROR_NOT_FOUND_USER_TABLE.getMessage()));

        List<OrderItemDto> orderItemDtos = requestCreateOrder.getItems().stream()
                .sorted(Comparator.comparing(item -> item.getId()))
                .collect(Collectors.toList());

        List<Item> findItems = itemQueryRepository.findAllByItemIds(orderItemDtos);

        List<Order> orders = Order.createOrder(orderItemDtos, findItems, findCustomer);
        List<Order> savedOrders = orderRepository.saveAll(orders);

        return ResponseCreateOrder.builder()
                .status(RESULT_CREATE_ORDER.getStatus())
                .message(RESULT_CREATE_ORDER.getMessage())
                .data(transferOrderDtos(findItems, savedOrders))
                .build();
    }

    private List<OrderDto> transferOrderDtos(List<Item> findItems, List<Order> savedOrders) {
        List<OrderDto> orderDtos = new ArrayList<>();

        for (int i = 0; i < findItems.size(); i++) {
            OrderDto orderDto = OrderDto.builder()
                    .id(savedOrders.get(i).getId())
                    .name(findItems.get(i).getName())
                    .count(savedOrders.get(i).getCount())
                    .orderPrice(savedOrders.get(i).getOrderPrice())
                    .request(savedOrders.get(i).getRequest())
                    .orderStatus(savedOrders.get(i).getOrderStatus())
                    .build();
            orderDtos.add(orderDto);
        }
        return orderDtos;
    }
}
