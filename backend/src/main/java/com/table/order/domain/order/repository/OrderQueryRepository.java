package com.table.order.domain.order.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.table.order.domain.customer.entity.QCustomer;
import com.table.order.domain.order.entity.Order;
import com.table.order.domain.order.entity.QOrder;
import com.table.order.domain.store.entity.QStore;
import com.table.order.domain.table.entity.QTable;
import com.table.order.domain.user.entity.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.table.order.domain.customer.entity.QCustomer.customer;
import static com.table.order.domain.order.entity.QOrder.*;
import static com.table.order.domain.order.entity.QOrder.order;
import static com.table.order.domain.store.entity.QStore.store;
import static com.table.order.domain.table.entity.QTable.table;
import static com.table.order.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Optional<Order> findByIdJoinCustomer(Long orderId, String username) {
        Order findOrder = queryFactory
                .selectFrom(order)
                .join(order.customer, customer)
                .where(order.id.eq(orderId), customer.username.eq(username))
                .fetchOne();
        return Optional.ofNullable(findOrder);
    }

    public Optional<Order> findByIdJoinTableStoreUser(Long orderId, String username) {
        Order findOrder = queryFactory
                .selectFrom(order)
                .join(order.table, table)
                .join(table.store, store)
                .join(store.user, user)
                .where(order.id.eq(orderId), user.username.eq(username))
                .fetchOne();
        return Optional.ofNullable(findOrder);
    }
}
