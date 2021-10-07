package com.table.order.domain.table.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.table.order.domain.item.entity.QItem;
import com.table.order.domain.order.dto.QOrderDto;
import com.table.order.domain.order.entity.OrderStatus;
import com.table.order.domain.order.entity.QOrder;
import com.table.order.domain.table.dto.QTableDto;
import com.table.order.domain.table.dto.TableDto;
import com.table.order.domain.table.entity.Table;
import com.table.order.domain.user.entity.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.table.order.domain.item.entity.QItem.item;
import static com.table.order.domain.order.entity.QOrder.order;
import static com.table.order.domain.store.entity.QStore.store;
import static com.table.order.domain.table.entity.QTable.*;
import static com.table.order.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class TableQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Optional<Table> findTableJoinStore(Long tableId, Long storeId) {
        Table findTable = queryFactory
                .select(table)
                .from(table)
                .join(table.store, store)
                .where(table.id.eq(tableId), store.id.eq(storeId))
                .fetchOne();
        return Optional.ofNullable(findTable);
    }

    public Page<Table> findAllJoinStoreUserOrder(String username, Pageable pageable) {
        QueryResults<Table> results = queryFactory
                .selectDistinct(table)
                .from(table)
                .join(table.store, store)
                .join(store.user, user)
                .leftJoin(table.orders, order).fetchJoin()
                .leftJoin(order.item, item).fetchJoin()
                .where(user.username.eq(username),
                        order.orderStatus.eq(OrderStatus.ORDER)
                                .or(order.orderStatus.eq(OrderStatus.COOK)
                                        .or(order.orderStatus.eq(OrderStatus.COOK_COMP)
                                                .or(order.orderStatus.isNull()))))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<Table> contents = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(contents, pageable, total);
    }

    public Optional<Table> findByIdJoinStoreUser(Long tableId, String username) {
        Table findTable = queryFactory
                .select(table)
                .from(table)
                .join(table.store, store)
                .join(store.user, user)
                .where(table.id.eq(tableId), user.username.eq(username))
                .fetchOne();

        return Optional.ofNullable(findTable);
    }
}
