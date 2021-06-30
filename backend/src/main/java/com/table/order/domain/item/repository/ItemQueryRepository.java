package com.table.order.domain.item.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.table.order.domain.item.dto.OrderItemDto;
import com.table.order.domain.item.entity.Item;
import com.table.order.domain.item.entity.QItem;
import com.table.order.domain.store.entity.QStore;
import com.table.order.domain.user.entity.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.table.order.domain.item.entity.QItem.*;
import static com.table.order.domain.item.entity.QItem.item;
import static com.table.order.domain.store.entity.QStore.store;
import static com.table.order.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class ItemQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<Item> findAllByItemIds(List<OrderItemDto> orderItemDtos) {
        BooleanBuilder builder = new BooleanBuilder();

        for (OrderItemDto orderItemDto : orderItemDtos) {
            if(orderItemDto.getId() != null)
                builder.or(item.id.eq(orderItemDto.getId()));
        }

        List<Item> findItems = queryFactory
                .select(item)
                .from(item)
                .where(builder)
                .fetch();
        return findItems;
    }

    public Optional<Item> findByIdJoinStoreUser(Long itemId, String username) {
        Item findItem = queryFactory
                .selectFrom(item)
                .join(item.store, store)
                .join(store.user, user)
                .where(item.id.eq(itemId), user.username.eq(username))
                .fetchOne();
        return Optional.ofNullable(findItem);
    }


}
