package com.table.order.domain.order.entity;

import com.table.order.domain.BaseEntity;
import com.table.order.domain.customer.entity.Customer;
import com.table.order.domain.item.entity.Item;
import com.table.order.domain.table.entity.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

    private void ordered() {
        this.orderStatus = OrderStatus.ORDER;
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
