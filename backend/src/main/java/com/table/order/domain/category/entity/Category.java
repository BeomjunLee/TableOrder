package com.table.order.domain.category.entity;

import com.table.order.domain.category.dto.request.RequestAddCategory;
import com.table.order.domain.category.dto.request.RequestUpdateCategory;
import com.table.order.domain.item.entity.Item;
import com.table.order.domain.store.entity.Store;
import com.table.order.domain.store.exception.CustomAccessDeniedException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.table.order.global.common.code.CustomErrorCode.ERROR_INVALID_STORE;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

    @Id @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @OneToMany(mappedBy = "category", orphanRemoval = true)
    private List<Item> items = new ArrayList<>();

    @Builder
    public Category(String name) {
        this.name = name;
    }

    public static Category createCategory(RequestAddCategory requestAddCategory, Store store) {
        Category category = Category.builder()
                .name(requestAddCategory.getName())
                .build();
        category.setStore(store);
        category.validate();
        return category;
    }

    private void validate() {
        if(!store.isValid())
            throw new CustomAccessDeniedException(ERROR_INVALID_STORE.getErrorCode(), ERROR_INVALID_STORE.getMessage());
    }

    public void updateCategory(RequestUpdateCategory requestUpdateCategory) {
        this.name = requestUpdateCategory.getName();
    }

    public void setStore(Store store) {
        this.store = store;
        store.getCategories().add(this);
    }
}
