package com.table.order.domain.category.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCategory is a Querydsl query type for Category
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QCategory extends EntityPathBase<Category> {

    private static final long serialVersionUID = -1698613738L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCategory category = new QCategory("category");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<com.table.order.domain.item.entity.Item, com.table.order.domain.item.entity.QItem> items = this.<com.table.order.domain.item.entity.Item, com.table.order.domain.item.entity.QItem>createList("items", com.table.order.domain.item.entity.Item.class, com.table.order.domain.item.entity.QItem.class, PathInits.DIRECT2);

    public final StringPath name = createString("name");

    public final com.table.order.domain.store.entity.QStore store;

    public QCategory(String variable) {
        this(Category.class, forVariable(variable), INITS);
    }

    public QCategory(Path<? extends Category> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCategory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCategory(PathMetadata metadata, PathInits inits) {
        this(Category.class, metadata, inits);
    }

    public QCategory(Class<? extends Category> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.store = inits.isInitialized("store") ? new com.table.order.domain.store.entity.QStore(forProperty("store"), inits.get("store")) : null;
    }

}

