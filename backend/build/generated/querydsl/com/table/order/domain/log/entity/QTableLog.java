package com.table.order.domain.log.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTableLog is a Querydsl query type for TableLog
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QTableLog extends EntityPathBase<TableLog> {

    private static final long serialVersionUID = 1837557082L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTableLog tableLog = new QTableLog("tableLog");

    public final com.table.order.domain.QBaseEntity _super = new com.table.order.domain.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final NumberPath<Integer> numberOfPeople = createNumber("numberOfPeople", Integer.class);

    public final ListPath<com.table.order.domain.order.entity.Order, com.table.order.domain.order.entity.QOrder> orders = this.<com.table.order.domain.order.entity.Order, com.table.order.domain.order.entity.QOrder>createList("orders", com.table.order.domain.order.entity.Order.class, com.table.order.domain.order.entity.QOrder.class, PathInits.DIRECT2);

    public final com.table.order.domain.store.entity.QStore store;

    public final NumberPath<Integer> totalPrice = createNumber("totalPrice", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public QTableLog(String variable) {
        this(TableLog.class, forVariable(variable), INITS);
    }

    public QTableLog(Path<? extends TableLog> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTableLog(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTableLog(PathMetadata metadata, PathInits inits) {
        this(TableLog.class, metadata, inits);
    }

    public QTableLog(Class<? extends TableLog> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.store = inits.isInitialized("store") ? new com.table.order.domain.store.entity.QStore(forProperty("store"), inits.get("store")) : null;
    }

}

