package com.table.order.domain.store.dto;

import com.table.order.domain.store.entity.StoreStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreDto {
    private Long id;
    private String name;
    private String description;
    private String licenseImage;
    private StoreStatus storeStatus;

    @Builder
    public StoreDto(Long id, String name, String description, String licenseImage, StoreStatus storeStatus) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.licenseImage = licenseImage;
        this.storeStatus = storeStatus;
    }
}
