package com.table.order.domain.category.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.table.order.domain.category.entity.Category;
import com.table.order.domain.customer.entity.QCustomer;
import com.table.order.domain.store.entity.QStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.table.order.domain.category.entity.QCategory.category;
import static com.table.order.domain.customer.entity.QCustomer.customer;
import static com.table.order.domain.item.entity.QItem.item;
import static com.table.order.domain.store.entity.QStore.store;
import static com.table.order.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class CategoryQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<Category> findAllJoinUserStoreItem(String username) {
        List<Category> categories = queryFactory
                .selectDistinct(category)
                .from(category)
                .join(category.store.user, user)
                .join(category.items, item).fetchJoin()
                .where(user.username.eq(username))
                .fetch();
        return categories;
    }

    public List<Category> findAllJoinCustomerStoreItem(String username) {
        List<Category> categories = queryFactory
                .selectDistinct(category)
                .from(category)
                .join(category.store, store)
                .join(customer).on(customer.store.id.eq(store.id)).fetchJoin()
                .join(category.items, item).fetchJoin()
                .where(customer.username.eq(username))
                .fetch();
        return categories;
    }
}
