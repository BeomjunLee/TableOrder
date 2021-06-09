package com.table.order.domain.store.entity;

import com.table.order.domain.BaseEntity;
import com.table.order.domain.category.entity.Category;
import com.table.order.domain.store.dto.request.RequestEnrollStore;
import com.table.order.domain.store.exception.CustomAccessDeniedException;
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

    @Column(nullable = false)
    private String licenseImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StoreStatus storeStatus;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Category> categories = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Builder
    public Store(String name, String description, String licenseImage, StoreStatus storeStatus, User user) {
        this.name = name;
        this.description = description;
        this.licenseImage = licenseImage;
        this.storeStatus = storeStatus;
        this.user = user;
    }

    public static Store createStore(RequestEnrollStore requestEnrollStore, User user) {
        Store store = Store.builder()
                .name(requestEnrollStore.getName())
                .description(requestEnrollStore.getDescription())
                .storeStatus(StoreStatus.INVALID)
                .user(user)
                .build();

        return store;
    }

    public boolean isValid() {
        if(storeStatus == StoreStatus.VALID)
            return true;
        return false;
    }

}
