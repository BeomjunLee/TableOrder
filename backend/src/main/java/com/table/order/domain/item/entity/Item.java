package com.table.order.domain.item.entity;

import com.table.order.domain.category.entity.Category;
import com.table.order.domain.BaseEntity;
import com.table.order.domain.item.dto.request.RequestAddItem;
import com.table.order.domain.store.entity.Store;
import com.table.order.domain.store.exception.CustomAccessDeniedException;
import com.table.order.global.common.code.CustomErrorCode;
import com.table.order.global.common.exception.CustomConflictException;
import com.table.order.global.common.exception.CustomIllegalArgumentException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.table.order.global.common.code.CustomErrorCode.ERROR_INVALID_STORE;
import static com.table.order.global.common.code.CustomErrorCode.ERROR_NOT_FOUND_CATEGORY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int price;

    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Builder
    public Item(String name, String description, int price, String image, Store store) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.store = store;
    }

    public static Item createItem(RequestAddItem requestAddItem, Store store) {
        Item item = Item.builder()
                .name(requestAddItem.getName())
                .description(requestAddItem.getDescription())
                .price(requestAddItem.getPrice())
                .image(requestAddItem.getImage())
                .store(store)
                .build();

        Category category = item.validate(requestAddItem);
        item.setCategory(category);

        return item;
    }

    private Category validate(RequestAddItem requestAddItem) {
        if(!store.isValid())
            throw new CustomConflictException(ERROR_INVALID_STORE.getErrorCode(), ERROR_INVALID_STORE.getMessage());

        List<Category> categories = filteringRequestCategory(requestAddItem);

        if(categories.size() == 0)
            throw new CustomIllegalArgumentException(ERROR_NOT_FOUND_CATEGORY.getErrorCode(), ERROR_NOT_FOUND_CATEGORY.getMessage());

        return categories.get(0);
    }

    private List<Category> filteringRequestCategory(RequestAddItem requestAddItem) {
        return store.getCategories().stream()
                .filter(category -> category.getId() == requestAddItem.getCategoryId())
                .collect(Collectors.toList());
    }

    public void setCategory(Category category) {
        this.category = category;
        category.getItems().add(this);
    }
}
