package com.table.order.domain.customer.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.table.order.domain.customer.dto.request.RequestLoginCustomer;
import com.table.order.domain.customer.entity.Customer;
import com.table.order.domain.customer.entity.QCustomer;
import com.table.order.domain.table.entity.QTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.table.order.domain.customer.entity.QCustomer.customer;
import static com.table.order.domain.store.entity.QStore.store;
import static com.table.order.domain.table.entity.QTable.table;

@Repository
@RequiredArgsConstructor
public class CustomerQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Optional<Customer> findByUsernameJoinStore(RequestLoginCustomer requestLoginCustomer) {
        Customer findCustomer = queryFactory
                .select(customer)
                .from(customer)
                .join(customer.store, store)
                .where(customer.username.eq(requestLoginCustomer.getUsername()),
                        store.id.eq(requestLoginCustomer.getStoreId()))
                .fetchOne();
        return Optional.ofNullable(findCustomer);
    }

    public Optional<Customer> findByUsernameJoinTable(String username) {
        Customer findCustomer = queryFactory
                .select(customer)
                .from(customer)
                .join(customer.table, table).fetchJoin()
                .where(customer.username.eq(username))
                .fetchOne();
        return Optional.ofNullable(findCustomer);
    }
}
