package com.table.order.domain.order;
import com.table.order.domain.customer.entity.Customer;
import com.table.order.domain.customer.repository.CustomerQueryRepository;
import com.table.order.domain.item.dto.OrderItemDto;
import com.table.order.domain.item.entity.Item;
import com.table.order.domain.item.repository.ItemQueryRepository;
import com.table.order.domain.order.dto.OrderDto;
import com.table.order.domain.order.dto.request.RequestCreateOrder;
import com.table.order.domain.order.dto.response.ResponseCreateOrder;
import com.table.order.domain.order.entity.Order;
import com.table.order.domain.order.entity.OrderStatus;
import com.table.order.domain.order.repository.OrderQueryRepository;
import com.table.order.domain.order.repository.OrderRepository;
import com.table.order.domain.order.service.OrderService;
import com.table.order.domain.table.entity.Table;
import com.table.order.domain.table.entity.TableStatus;
import com.table.order.global.common.exception.CustomConflictException;
import com.table.order.global.common.exception.CustomIllegalArgumentException;
import com.table.order.global.common.response.ResponseResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.table.order.global.common.code.CustomErrorCode.*;
import static com.table.order.global.common.code.ResultCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @InjectMocks
    private OrderService orderService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ItemQueryRepository itemQueryRepository;
    @Mock
    private CustomerQueryRepository customerQueryRepository;
    @Mock
    private OrderQueryRepository orderQueryRepository;

    public RequestCreateOrder requestCreateOrder;
    private ResponseCreateOrder responseCreateOrder;
    private Order order;
    private List<Order> orders = new ArrayList<>();
    private Item item;
    private List<Item> items = new ArrayList<>();
    private OrderItemDto orderItemDto;
    private OrderDto orderDto;

    @BeforeEach
    public void init() {
        List<OrderItemDto> orderItemDtos = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            orderItemDto = OrderItemDto.builder()
                    .id((long) i)
                    .count(i)
                    .build();
            orderItemDtos.add(orderItemDto);
        }

        requestCreateOrder = RequestCreateOrder.builder()
                .items(orderItemDtos)
                .request("?????????????????????")
                .build();
        List<OrderDto> orderDtos = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            orderDto = OrderDto.builder()
                    .id(null)
                    .name("??????"+i)
                    .orderPrice(1000 * i)
                    .count(i)
                    .orderStatus(OrderStatus.ORDER)
                    .request("?????????????????????")
                    .build();
            orderDtos.add(orderDto);
        }
        responseCreateOrder = ResponseCreateOrder.builder()
                .status(RESULT_CREATE_ORDER.getStatus())
                .message(RESULT_CREATE_ORDER.getMessage())
                .data(orderDtos)
                .build();

        for (int i = 1; i <= 3; i++) {
            item = Item.builder()
                    .name("??????" + i)
                    .price(1000)
                    .description("?????? ??????")
                    .image("?????? ?????????")
                    .build();
            items.add(item);

            order = Order.builder()
                    .request("?????????????????????")
                    .orderPrice(item.getPrice() * i)
                    .orderStatus(OrderStatus.ORDER)
                    .count(i)
                    .item(item)
                    .build();
            orders.add(order);
        }
    }

    @Test
    @DisplayName("?????? ?????? ?????????")
    public void createOrder() throws Exception{
        //given
        Table table = Table.builder()
                .name("?????????")
                .numberOfPeople(5)
                .tableStatus(TableStatus.OPEN)
                .build();
        Customer customer = Customer.builder()
                .username("test")
                .table(table)
                .build();

        given(customerQueryRepository.findByUsernameJoinTable(anyString())).willReturn(Optional.ofNullable(customer));
        given(itemQueryRepository.findAllByItemIds(anyList())).willReturn(items);
        given(orderRepository.saveAll(anyList())).willReturn(orders);
        //when
        ResponseCreateOrder response = orderService.createOrder(requestCreateOrder, anyString());

        //then
        assertThat(response).usingRecursiveComparison().isEqualTo(responseCreateOrder);
    }

    @Test
    @DisplayName("?????? ?????? ?????? ????????? (?????? ????????? ?????? ??????)")
    public void createOrderNotFoundCustomerTable() throws Exception{
        //given
        given(customerQueryRepository.findByUsernameJoinTable(anyString())).willReturn(Optional.ofNullable(null));

        //when then
        assertThatThrownBy(() -> {
            orderService.createOrder(requestCreateOrder, anyString());
        }).isInstanceOf(CustomIllegalArgumentException.class).hasMessageContaining(ERROR_NOT_FOUND_CUSTOMER_TABLE.getMessage());
    }

    @Test
    @DisplayName("?????? ?????? ?????? ????????? (?????? ????????? ???????????????)")
    public void createOrderNotCompTable() throws Exception{
        //given
        Table table = Table.builder()
                .name("?????????")
                .numberOfPeople(5)
                .tableStatus(TableStatus.COMP)
                .build();
        Customer customer = Customer.builder()
                .username("test")
                .table(table)
                .build();

        given(customerQueryRepository.findByUsernameJoinTable(anyString())).willReturn(Optional.ofNullable(customer));
        given(itemQueryRepository.findAllByItemIds(anyList())).willReturn(items);

        //when then
        assertThatThrownBy(() -> {
            orderService.createOrder(requestCreateOrder, anyString());
        }).isInstanceOf(CustomConflictException.class).hasMessageContaining(ERROR_ALREADY_COMP.getMessage());
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????????")
    public void cancelOrderCustomer() throws Exception{
        //given
        ResponseResult responseResult = ResponseResult.builder()
                .status(RESULT_CANCEL_ORDER.getStatus())
                .message(RESULT_CANCEL_ORDER.getMessage())
                .build();

        given(orderQueryRepository.findByIdJoinCustomer(anyLong(), anyString())).willReturn(Optional.ofNullable(order));

        //when
        ResponseResult response = orderService.cancelOrderCustomer(anyLong(), anyString());

        //then
        assertThat(response).usingRecursiveComparison().isEqualTo(responseResult);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCEL);
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????????")
    public void cancelOrderUser() throws Exception{
        //given
        ResponseResult responseResult = ResponseResult.builder()
                .status(RESULT_CANCEL_ORDER.getStatus())
                .message(RESULT_CANCEL_ORDER.getMessage())
                .build();

        given(orderQueryRepository.findByIdJoinTableStoreUser(anyLong(), anyString())).willReturn(Optional.ofNullable(order));

        //when
        ResponseResult response = orderService.cancelOrderUser(anyLong(), anyString());

        //then
        assertThat(response).usingRecursiveComparison().isEqualTo(responseResult);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCEL);
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? ????????? (?????? ???????????????)")
    public void cancelOrderFailCustomerByCook() throws Exception{
        //given
        order = Order.builder()
                .request("?????????????????????")
                .orderPrice(item.getPrice())
                .orderStatus(OrderStatus.COOK)
                .item(item)
                .build();

        given(orderQueryRepository.findByIdJoinCustomer(anyLong(), anyString())).willReturn(Optional.ofNullable(order));

        //when then
        assertThatThrownBy(() -> {
            orderService.cancelOrderCustomer(anyLong(), anyString());
        }).isInstanceOf(CustomConflictException.class).hasMessageContaining(ERROR_DENIED_CANCEL_ORDER_BY_COOK.getMessage());
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? ????????? (?????? ?????????)")
    public void cancelOrderFailCustomerByComp() throws Exception{
        //given
        order = Order.builder()
                .request("?????????????????????")
                .orderPrice(item.getPrice())
                .orderStatus(OrderStatus.COMP)
                .item(item)
                .build();

        given(orderQueryRepository.findByIdJoinCustomer(anyLong(), anyString())).willReturn(Optional.ofNullable(order));

        //when then
        assertThatThrownBy(() -> {
            orderService.cancelOrderCustomer(anyLong(), anyString());
        }).isInstanceOf(CustomConflictException.class).hasMessageContaining(ERROR_DENIED_CANCEL_ORDER_BY_COMP.getMessage());
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? ????????? (?????? ????????? ?????? ??? ??????)")
    public void cancelOrderFailCustomerNotFoundOrder() throws Exception{
        //given
        given(orderQueryRepository.findByIdJoinCustomer(anyLong(), anyString())).willReturn(Optional.ofNullable(null));

        //when then
        assertThatThrownBy(() -> {
            orderService.cancelOrderCustomer(anyLong(), anyString());
        }).isInstanceOf(CustomIllegalArgumentException.class).hasMessageContaining(ERROR_NOT_FOUND_ORDER.getMessage());
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? ????????? (?????? ????????? ?????? ??? ??????)")
    public void cancelOrderFailUserNotFoundOrder() throws Exception{
        //given
        given(orderQueryRepository.findByIdJoinTableStoreUser(anyLong(), anyString())).willReturn(Optional.ofNullable(null));

        //when then
        assertThatThrownBy(() -> {
            orderService.cancelOrderUser(anyLong(), anyString());
        }).isInstanceOf(CustomIllegalArgumentException.class).hasMessageContaining(ERROR_NOT_FOUND_ORDER.getMessage());
    }

}