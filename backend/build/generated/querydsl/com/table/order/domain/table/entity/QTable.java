package com.table.order.domain.table.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTable is a Querydsl query type for Table
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QTable extends EntityPathBase<Table> {

    private static final long serialVersionUID = -1174659584L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTable table = new QTable("table");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final NumberPath<Integer> numberOfPeople = createNumber("numberOfPeople", Integer.class);

    public final ListPath<com.table.order.domain.order.entity.Order, com.table.order.domain.order.entity.QOrder> orders = this.<com.table.order.domain.order.entity.Order, com.table.order.domain.order.entity.QOrder>createList("orders", com.table.order.domain.order.entity.Order.class, com.table.order.domain.order.entity.QOrder.class, PathInits.DIRECT2);

    public final com.table.order.domain.store.entity.QStore store;

    public final EnumPath<TableStatus> tableStatus = createEnum("tableStatus", TableStatus.class);

    public final NumberPath<Integer> totalPrice = createNumber("totalPrice", Integer.class);

    public QTable(String variable) {
        this(Table.class, forVariable(variable), INITS);
    }

    public QTable(Path<? extends Table> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTable(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTable(PathMetadata metadata, PathInits inits) {
        this(Table.class, metadata, inits);
    }

    public QTable(Class<? extends Table> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.store = inits.isInitialized("store") ? new com.table.order.domain.store.entity.QStore(forProperty("store"), inits.get("store")) : null;
    }

}

