package com.table.order.domain.order.service;
import com.table.order.domain.customer.entity.Customer;
import com.table.order.domain.customer.repository.CustomerQueryRepository;
import com.table.order.domain.item.dto.OrderItemDto;
import com.table.order.domain.item.entity.Item;
import com.table.order.domain.item.repository.ItemQueryRepository;
import com.table.order.domain.order.dto.OrderDto;
import com.table.order.domain.order.dto.request.RequestChangeOrderStatus;
import com.table.order.domain.order.dto.request.RequestCreateOrder;
import com.table.order.domain.order.dto.response.ResponseCreateOrder;
import com.table.order.domain.order.entity.Order;
import com.table.order.domain.order.repository.OrderQueryRepository;
import com.table.order.domain.order.repository.OrderRepository;
import com.table.order.global.common.exception.CustomIllegalArgumentException;
import com.table.order.global.common.response.ResponseResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.table.order.global.common.code.CustomErrorCode.ERROR_NOT_FOUND_CUSTOMER_TABLE;
import static com.table.order.global.common.code.CustomErrorCode.ERROR_NOT_FOUND_ORDER;
import static com.table.order.global.common.code.ResultCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;
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
                .orElseThrow(() -> new CustomIllegalArgumentException(ERROR_NOT_FOUND_CUSTOMER_TABLE.getErrorCode(), ERROR_NOT_FOUND_CUSTOMER_TABLE.getMessage()));

        List<OrderItemDto> orderItemDtos = requestCreateOrder.getItems().stream()
                .sorted(Comparator.comparing(item -> item.getId()))
                .collect(Collectors.toList());

        List<Item> findItems = itemQueryRepository.findAllByItemIds(orderItemDtos);

        List<Order> orders = Order.createOrder(requestCreateOrder, findItems, findCustomer);
        List<Order> savedOrders = orderRepository.saveAll(orders);

        return ResponseCreateOrder.builder()
                .status(RESULT_CREATE_ORDER.getStatus())
                .message(RESULT_CREATE_ORDER.getMessage())
                .data(transferOrderDtos(findItems, savedOrders))
                .build();
    }

    /**
     * OrderDto 리스트로 변환
     * @param findItems 주문한 메뉴 목록
     * @param savedOrders save 한 주문 목록
     * @return 응답 dto
     */
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

    /**
     * 주문 취소 -손님-
     * @param orderId 주문 고유 id
     * @param username 손님 아이디
     * @return 응답 dto
     */
    public ResponseResult cancelOrderCustomer(Long orderId, String username) {
        Order findOrder = orderQueryRepository.findByIdJoinCustomer(orderId, username)
                .orElseThrow(() -> new CustomIllegalArgumentException(ERROR_NOT_FOUND_ORDER.getErrorCode(), ERROR_NOT_FOUND_ORDER.getMessage()));
        findOrder.customerCanceled();
        return ResponseResult.builder()
                .status(RESULT_CANCEL_ORDER.getStatus())
                .message(RESULT_CANCEL_ORDER.getMessage())
                .build();
    }

    /**
     * 주문 취소 -회원-
     * @param orderId 주문 고유 id
     * @param username 회원 아이디
     * @return 응답 dto
     */
    public ResponseResult cancelOrderUser(Long orderId, String username) {
        Order findOrder = orderQueryRepository.findByIdJoinTableStoreUser(orderId, username)
                .orElseThrow(() -> new CustomIllegalArgumentException(ERROR_NOT_FOUND_ORDER.getErrorCode(), ERROR_NOT_FOUND_ORDER.getMessage()));
        findOrder.userCanceled();

        return ResponseResult.builder()
                .status(RESULT_CANCEL_ORDER.getStatus())
                .message(RESULT_CANCEL_ORDER.getMessage())
                .build();
    }

    /**
     * 주문 상태 변경 (조리중)
     * @param requestChangeOrderStatus 주문 고유 id 리스트
     * @param username 회원 아이디
     * @return 응답 dto
     */
    public ResponseResult changeOrderStatusCooked(RequestChangeOrderStatus requestChangeOrderStatus, String username) {
        orderQueryRepository.findByIdsJoinTableStoreUserBooleanBuilderOrderStatus(requestChangeOrderStatus.getIds(), username)
                .stream().forEach(o -> o.cooked());

        return ResponseResult.builder()
                .status(RESULT_COOK_ORDER.getStatus())
                .message(RESULT_COOK_ORDER.getMessage())
                .build();
    }

    /**
     * 주문 상태 변경 (조리 완료)
     * @param requestChangeOrderStatus 주문 고유 id 리스트
     * @param username 회원 아이디
     * @return 응답 dto
     */
    public ResponseResult changeOrderStatusCookComp(RequestChangeOrderStatus requestChangeOrderStatus, String username) {
        orderQueryRepository.findByIdsJoinTableStoreUserBooleanBuilderOrderStatus(requestChangeOrderStatus.getIds(), username)
                .stream().forEach(o -> o.cookComp());

        return ResponseResult.builder()
                .status(RESULT_COOK_COMP_ORDER.getStatus())
                .message(RESULT_COOK_COMP_ORDER.getMessage())
                .build();
    }

    /**
     * 주문 상태 변경 (조리 완료)
     * @param requestChangeOrderStatus 주문 고유 id 리스트
     * @param username 회원 아이디
     * @return 응답 dto
     */
    public ResponseResult changeOrderStatusComp(RequestChangeOrderStatus requestChangeOrderStatus, String username) {
        orderQueryRepository.findByIdsJoinTableStoreUserBooleanBuilderOrderStatus(requestChangeOrderStatus.getIds(), username)
                .stream().forEach(o -> o.comp());

        return ResponseResult.builder()
                .status(RESULT_COMP_ORDER.getStatus())
                .message(RESULT_COMP_ORDER.getMessage())
                .build();
    }
}
