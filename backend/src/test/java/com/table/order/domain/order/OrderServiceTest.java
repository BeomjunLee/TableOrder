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
import com.table.order.domain.order.repository.OrderRepository;
import com.table.order.domain.order.service.OrderService;
import com.table.order.domain.store.exception.CustomAccessDeniedException;
import com.table.order.domain.table.entity.Table;
import com.table.order.domain.table.entity.TableStatus;
import com.table.order.global.common.exception.CustomIllegalArgumentException;
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
import static com.table.order.global.common.code.CustomErrorCode.ERROR_ALREADY_COMP;
import static com.table.order.global.common.code.CustomErrorCode.ERROR_NOT_FOUND_CUSTOMER_TABLE;
import static com.table.order.global.common.code.ResultCode.RESULT_CREATE_ORDER;
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
                .request("잘부탁드립니다")
                .build();
        List<OrderDto> orderDtos = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            orderDto = OrderDto.builder()
                    .id(null)
                    .name("메뉴"+i)
                    .orderPrice(1000 * i)
                    .count(i)
                    .orderStatus(OrderStatus.ORDER)
                    .request("잘부탁드립니다")
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
                    .name("메뉴" + i)
                    .price(1000)
                    .description("메뉴 설명")
                    .image("메뉴 이미지")
                    .build();
            items.add(item);

            order = Order.builder()
                    .request("잘부탁드립니다")
                    .orderPrice(item.getPrice() * i)
                    .orderStatus(OrderStatus.ORDER)
                    .count(i)
                    .item(item)
                    .build();
            orders.add(order);
        }
    }

    @Test
    @DisplayName("주문 생성 테스트")
    public void createOrder() throws Exception{
        //given
        Table table = Table.builder()
                .name("테이블")
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
    @DisplayName("주문 생성 실패 테스트 (손님 테이블 찾기 실패)")
    public void createOrderNotFoundCustomerTable() throws Exception{
        //given
        given(customerQueryRepository.findByUsernameJoinTable(anyString())).willReturn(Optional.ofNullable(null));

        //when then
        assertThatThrownBy(() -> {
            orderService.createOrder(requestCreateOrder, anyString());
        }).isInstanceOf(CustomIllegalArgumentException.class).hasMessageContaining(ERROR_NOT_FOUND_CUSTOMER_TABLE.getMessage());
    }

    @Test
    @DisplayName("주문 생성 실패 테스트 (이미 결제된 테이블일시)")
    public void createOrderNotCompTable() throws Exception{
        //given
        Table table = Table.builder()
                .name("테이블")
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
        }).isInstanceOf(CustomAccessDeniedException.class).hasMessageContaining(ERROR_ALREADY_COMP.getMessage());
    }

}