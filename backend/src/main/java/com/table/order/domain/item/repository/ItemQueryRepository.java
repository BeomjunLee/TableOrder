package com.table.order.domain.item.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.table.order.domain.item.dto.OrderItemDto;
import com.table.order.domain.item.entity.Item;
import com.table.order.domain.item.entity.QItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.table.order.domain.item.entity.QItem.item;

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


}
