package com.table.order.domain.store.repository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.table.order.domain.store.entity.QStore;
import com.table.order.domain.store.entity.Store;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import static com.table.order.domain.store.entity.QStore.*;
import static com.table.order.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class StoreQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Optional<Store> findByUsernameJoinUser(String username) {
        Store findStore = queryFactory
                .select(store)
                .from(store)
                .join(store.user, user)
                .where(user.username.eq(username))
                .fetchOne();
        return Optional.ofNullable(findStore);
    }

}
