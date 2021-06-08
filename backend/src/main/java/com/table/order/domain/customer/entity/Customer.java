package com.table.order.domain.customer.entity;

import com.table.order.domain.BaseEntity;
import com.table.order.domain.order.entity.Order;
import com.table.order.domain.store.entity.Store;
import com.table.order.domain.table.entity.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "customer_id")
    private Long id;

    @Column(nullable = false)
    private String username;

    private int visitCount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CustomerStatus customerStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id")
    private Table table;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @OneToMany(mappedBy = "customer")
    private List<Order> orders = new ArrayList<>();

    @Builder
    public Customer(String username, CustomerStatus customerStatus, Table table, Store store) {
        this.username = username;
        this.customerStatus = customerStatus;
        this.table = table;
        this.store = store;
    }


    public static Customer createCustomer(String username, Table table, Store store) {
        Customer customer = Customer.builder()
                .username(username)
                .customerStatus(CustomerStatus.IN)
                .table(table)
                .store(store)
                .build();

        customer.validate();

        return customer;
    }

    private void validate() {
        if(!table.isOpen())
            throw new IllegalArgumentException("사용중인 테이블입니다");
    }

}
