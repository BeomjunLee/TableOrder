package com.table.order.domain.store.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStore is a Querydsl query type for Store
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QStore extends EntityPathBase<Store> {

    private static final long serialVersionUID = -2121452320L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStore store = new QStore("store");

    public final com.table.order.domain.QBaseEntity _super = new com.table.order.domain.QBaseEntity(this);

    public final ListPath<com.table.order.domain.category.entity.Category, com.table.order.domain.category.entity.QCategory> categories = this.<com.table.order.domain.category.entity.Category, com.table.order.domain.category.entity.QCategory>createList("categories", com.table.order.domain.category.entity.Category.class, com.table.order.domain.category.entity.QCategory.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public final com.table.order.domain.user.entity.QUser user;

    public QStore(String variable) {
        this(Store.class, forVariable(variable), INITS);
    }

    public QStore(Path<? extends Store> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QStore(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QStore(PathMetadata metadata, PathInits inits) {
        this(Store.class, metadata, inits);
    }

    public QStore(Class<? extends Store> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.table.order.domain.user.entity.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

