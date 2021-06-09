package com.table.order.domain.table.entity;

import com.table.order.domain.order.entity.Order;
import com.table.order.domain.store.entity.Store;
import com.table.order.domain.store.exception.CustomAccessDeniedException;
import com.table.order.domain.table.dto.request.RequestAddTable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.table.order.global.common.code.CustomErrorCode.ERROR_INVALID_STORE;

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

    public static Table createTable(RequestAddTable requestAddTable, Store store) {
        Table table = Table.builder()
                .name(requestAddTable.getName())
                .numberOfPeople(requestAddTable.getNumberOfPeople())
                .tableStatus(TableStatus.OPEN)
                .store(store)
                .build();

        table.validate();

        return table;
    }

    private void validate() {
        if(!store.isValid())
            throw new CustomAccessDeniedException(ERROR_INVALID_STORE.getErrorCode(), ERROR_INVALID_STORE.getMessage());
    }

    public boolean isOpen() {
        if(tableStatus == TableStatus.OPEN)
            return true;
        return false;
    }


}
