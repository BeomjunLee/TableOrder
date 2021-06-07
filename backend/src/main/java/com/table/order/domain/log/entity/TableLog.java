package com.table.order.domain.log.entity;

import com.table.order.domain.BaseEntity;
import com.table.order.domain.order.entity.Order;
import com.table.order.domain.store.entity.Store;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TableLog extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "table_log_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int numberOfPeople;

    @Column(nullable = false)
    private int totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();
}
