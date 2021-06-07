package com.table.order.domain.table.entity;

import com.table.order.domain.store.entity.Store;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Table {

    @Id @GeneratedValue
    @Column(name = "table_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int numberOfPeople;

    @Column(nullable = false)
    private int totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TableStatus tableStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;
}
