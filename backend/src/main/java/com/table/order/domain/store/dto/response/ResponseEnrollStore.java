package com.table.order.domain.store.dto.response;

import com.table.order.domain.store.dto.StoreDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResponseEnrollStore {
    private int status;
    private String message;
    private StoreDto data;

    @Builder
    public ResponseEnrollStore(int status, String message, StoreDto data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
