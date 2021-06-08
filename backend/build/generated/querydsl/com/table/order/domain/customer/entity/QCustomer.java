package com.table.order.domain.customer.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCustomer is a Querydsl query type for Customer
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QCustomer extends EntityPathBase<Customer> {

    private static final long serialVersionUID = 803011542L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCustomer customer = new QCustomer("customer");

    public final com.table.order.domain.QBaseEntity _super = new com.table.order.domain.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final EnumPath<CustomerStatus> customerStatus = createEnum("customerStatus", CustomerStatus.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<com.table.order.domain.order.entity.Order, com.table.order.domain.order.entity.QOrder> orders = this.<com.table.order.domain.order.entity.Order, com.table.order.domain.order.entity.QOrder>createList("orders", com.table.order.domain.order.entity.Order.class, com.table.order.domain.order.entity.QOrder.class, PathInits.DIRECT2);

    public final com.table.order.domain.store.entity.QStore store;

    public final com.table.order.domain.table.entity.QTable table;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public final StringPath username = createString("username");

    public final NumberPath<Integer> visitCount = createNumber("visitCount", Integer.class);

    public QCustomer(String variable) {
        this(Customer.class, forVariable(variable), INITS);
    }

    public QCustomer(Path<? extends Customer> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCustomer(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCustomer(PathMetadata metadata, PathInits inits) {
        this(Customer.class, metadata, inits);
    }

    public QCustomer(Class<? extends Customer> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.store = inits.isInitialized("store") ? new com.table.order.domain.store.entity.QStore(forProperty("store"), inits.get("store")) : null;
        this.table = inits.isInitialized("table") ? new com.table.order.domain.table.entity.QTable(forProperty("table"), inits.get("table")) : null;
    }

}

