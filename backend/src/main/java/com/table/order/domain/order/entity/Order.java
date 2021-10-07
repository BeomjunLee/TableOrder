package com.table.order.domain.order.entity;

import com.table.order.domain.BaseEntity;
import com.table.order.domain.customer.entity.Customer;
import com.table.order.domain.item.entity.Item;
import com.table.order.domain.order.dto.request.RequestCreateOrder;
import com.table.order.domain.table.entity.Table;
import com.table.order.global.common.exception.CustomConflictException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.table.order.global.common.code.CustomErrorCode.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@javax.persistence.Table(name = "orders")
public class Order extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @Column(nullable = false)
    private int orderPrice;

    @Column(nullable = false)
    private int count;

    @Column(nullable = false)
    private String request;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id")
    private Table table;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Builder
    public Order(int orderPrice, int count, String request, OrderStatus orderStatus, Item item) {
        this.orderPrice = orderPrice;
        this.count = count;
        this.request = request;
        this.orderStatus = orderStatus;
        this.item = item;
    }

    public static List<Order> createOrder(RequestCreateOrder requestCreateOrder, List<Item> items, Customer customer) {
        List<Order> orders = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            Order order = Order.builder()
                    .orderPrice(items.get(i).getPrice() * requestCreateOrder.getItems().get(i).getCount())
                    .count(requestCreateOrder.getItems().get(i).getCount())
                    .request(requestCreateOrder.getRequest())
                    .orderStatus(OrderStatus.ORDER)
                    .item(items.get(i))
                    .build();
            order.setTable(customer.getTable());
            order.setCustomer(customer);
            order.validate();

            order.table.inUsed();
            orders.add(order);
        }

        return orders;
    }

    private void validate() {
        if(table.isComp())
            throw new CustomConflictException(ERROR_ALREADY_COMP.getErrorCode(), ERROR_ALREADY_COMP.getMessage());
    }

    public void customerCanceled() {
        if(orderStatus == OrderStatus.COOK)
            throw new CustomConflictException(ERROR_DENIED_CANCEL_ORDER_BY_COOK.getErrorCode(), ERROR_DENIED_CANCEL_ORDER_BY_COOK.getMessage());
        if(orderStatus == OrderStatus.COMP)
            throw new CustomConflictException(ERROR_DENIED_CANCEL_ORDER_BY_COMP.getErrorCode(), ERROR_DENIED_CANCEL_ORDER_BY_COMP.getMessage());
        this.orderStatus = OrderStatus.CANCEL;
    }

    public void userCanceled() {
        this.orderStatus = OrderStatus.CANCEL;
    }

    public void cooked() {
        if(orderStatus == OrderStatus.COMP)
            throw new CustomConflictException(ERROR_DENIED_CANCEL_ORDER_BY_COMP.getErrorCode(), ERROR_DENIED_CANCEL_ORDER_BY_COMP.getMessage());
        this.orderStatus = OrderStatus.COOK;
    }

    public void cookComp() {
        if(orderStatus == OrderStatus.COMP)
            throw new CustomConflictException(ERROR_DENIED_CANCEL_ORDER_BY_COMP.getErrorCode(), ERROR_DENIED_CANCEL_ORDER_BY_COMP.getMessage());
        this.orderStatus = OrderStatus.COOK_COMP;
    }

    public void comp() {
        this.orderStatus = OrderStatus.COMP;
    }

    public void setTable(Table table) {
        this.table = table;
        table.getOrders().add(this);
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        customer.getOrders().add(this);
    }
}
