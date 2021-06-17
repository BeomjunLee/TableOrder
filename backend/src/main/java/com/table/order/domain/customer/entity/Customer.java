package com.table.order.domain.customer.entity;

import com.table.order.domain.BaseEntity;
import com.table.order.domain.customer.dto.request.RequestLoginCustomer;
import com.table.order.domain.order.entity.Order;
import com.table.order.domain.store.entity.Store;
import com.table.order.domain.store.exception.CustomAccessDeniedException;
import com.table.order.domain.table.entity.Table;
import com.table.order.global.common.code.CustomErrorCode;
import com.table.order.global.common.exception.CustomConflictException;
import com.table.order.global.common.exception.CustomIllegalArgumentException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.table.order.global.common.code.CustomErrorCode.ERROR_INVALID_STORE;
import static com.table.order.global.common.code.CustomErrorCode.ERROR_IN_USE_TABLE;


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
    public Customer(String username, int visitCount, CustomerStatus customerStatus, Table table, Store store) {
        this.username = username;
        this.visitCount = visitCount;
        this.customerStatus = customerStatus;
        this.table = table;
        this.store = store;
    }

    public static Customer createCustomer(String username, Table table, Store store) {
        Customer customer = Customer.builder()
                .username(username)
                .visitCount(1)
                .customerStatus(CustomerStatus.IN)
                .table(table)
                .store(store)
                .build();

        customer.validate();

        return customer;
    }

    private void validate() {
        if(!table.isOpen())
            throw new CustomConflictException(ERROR_IN_USE_TABLE.getErrorCode(), ERROR_IN_USE_TABLE.getMessage());
        if(!store.isValid())
            throw new CustomConflictException(ERROR_INVALID_STORE.getErrorCode(), ERROR_INVALID_STORE.getMessage());
    }

    public boolean isInUse() {
        validate();

        if(customerStatus == CustomerStatus.IN)
            return true;
        return false;
    }

    public boolean isVisited() {
        validate();

        if(!isInUse())
            return true;
        return false;
    }

    public void updateTable(Table table) {
        this.table = table;
        this.visitCount++;
        this.customerStatus = CustomerStatus.IN;
    }
}
