package com.table.order.domain.store.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestEnrollStore {
    private String name;
    private String description;
    private String licenseImage;

    @Builder
    public RequestEnrollStore(String name, String description, String licenseImage) {
        this.name = name;
        this.description = description;
        this.licenseImage = licenseImage;
    }
}
