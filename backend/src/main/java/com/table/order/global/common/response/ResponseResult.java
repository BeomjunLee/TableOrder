package com.table.order.global.common.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResponseResult {
    private int status;
    private String message;

    @Builder
    public ResponseResult(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
