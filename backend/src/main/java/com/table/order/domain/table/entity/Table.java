package com.table.order.domain.table.entity;

import com.table.order.domain.order.entity.Order;
import com.table.order.domain.store.entity.Store;
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
@javax.persistence.Table(name = "order_table")
public class Table {

    @Id @GeneratedValue
    @Column(name = "table_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    private int numberOfPeople;

    private int totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TableStatus tableStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    @Builder
    public Table(String name, int numberOfPeople, TableStatus tableStatus, Store store) {
        this.name = name;
        this.numberOfPeople = numberOfPeople;
        this.tableStatus = tableStatus;
        this.store = store;
    }

    public boolean isOpen() {
        if(tableStatus == TableStatus.OPEN)
            return true;
        return false;
    }


}
