package com.table.order.domain.customer.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.table.order.domain.customer.dto.request.RequestLoginCustomer;
import com.table.order.domain.customer.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.table.order.domain.customer.entity.QCustomer.customer;
import static com.table.order.domain.store.entity.QStore.store;

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
}
