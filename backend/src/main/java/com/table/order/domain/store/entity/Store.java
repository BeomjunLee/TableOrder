package com.table.order.domain.store.entity;

import com.table.order.domain.BaseEntity;
import com.table.order.domain.category.entity.Category;
import com.table.order.domain.store.dto.request.RequestEnrollStore;
import com.table.order.domain.user.entity.User;
import com.table.order.global.common.code.CustomErrorCode;
import com.table.order.global.common.exception.CustomIllegalArgumentException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.table.order.global.common.code.CustomErrorCode.ERROR_ACCESS_DENIED;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "store_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Category> categories = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Store(String name, String description, User user) {
        this.name = name;
        this.description = description;
        this.user = user;
    }

    public static Store createStore(RequestEnrollStore requestEnrollStore, User user) {
        Store store = Store.builder()
                .name(requestEnrollStore.getName())
                .description(requestEnrollStore.getDescription())
                .user(user)
                .build();
        store.validate();

        return store;
    }

    private void validate() {
        if(!user.isUser())
            throw new CustomIllegalArgumentException(ERROR_ACCESS_DENIED.getErrorCode(), ERROR_ACCESS_DENIED.getMessage());
    }

}
