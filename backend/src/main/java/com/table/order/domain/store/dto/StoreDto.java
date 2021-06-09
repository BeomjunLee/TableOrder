package com.table.order.domain.store.dto;

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

    @Builder
    public StoreDto(Long id, String name, String description, String licenseImage) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.licenseImage = licenseImage;
    }
}
