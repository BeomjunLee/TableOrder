package com.table.order.domain.order.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOrder is a Querydsl query type for Order
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QOrder extends EntityPathBase<Order> {

    private static final long serialVersionUID = 76729856L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOrder order = new QOrder("order1");

    public final com.table.order.domain.QBaseEntity _super = new com.table.order.domain.QBaseEntity(this);

    public final NumberPath<Integer> count = createNumber("count", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final com.table.order.domain.customer.entity.QCustomer customer;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.table.order.domain.item.entity.QItem item;

    public final NumberPath<Integer> orderPrice = createNumber("orderPrice", Integer.class);

    public final EnumPath<OrderStatus> orderStatus = createEnum("orderStatus", OrderStatus.class);

    public final StringPath request = createString("request");

    public final com.table.order.domain.table.entity.QTable table;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public QOrder(String variable) {
        this(Order.class, forVariable(variable), INITS);
    }

    public QOrder(Path<? extends Order> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOrder(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOrder(PathMetadata metadata, PathInits inits) {
        this(Order.class, metadata, inits);
    }

    public QOrder(Class<? extends Order> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.customer = inits.isInitialized("customer") ? new com.table.order.domain.customer.entity.QCustomer(forProperty("customer"), inits.get("customer")) : null;
        this.item = inits.isInitialized("item") ? new com.table.order.domain.item.entity.QItem(forProperty("item"), inits.get("item")) : null;
        this.table = inits.isInitialized("table") ? new com.table.order.domain.table.entity.QTable(forProperty("table"), inits.get("table")) : null;
    }

}

