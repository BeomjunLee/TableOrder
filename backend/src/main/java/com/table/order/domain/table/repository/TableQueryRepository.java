package com.table.order.domain.table.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.table.order.domain.table.entity.Table;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.table.order.domain.store.entity.QStore.store;
import static com.table.order.domain.table.entity.QTable.*;

@Repository
@RequiredArgsConstructor
public class TableQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Optional<Table> findTableJoinStore(Long tablaId, Long storeId) {
        Table findTable = queryFactory
                .select(table)
                .from(table)
                .join(table.store, store)
                .where(table.id.eq(tablaId), store.id.eq(storeId))
                .fetchOne();
        return Optional.ofNullable(findTable);
    }
}
