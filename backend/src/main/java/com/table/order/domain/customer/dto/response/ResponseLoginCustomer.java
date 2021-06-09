package com.table.order.domain.customer.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ResponseLoginCustomer {
    private int status;
    private String message;
    private String accessToken;
    private LocalDateTime expiredAt;

    @Builder
    public ResponseLoginCustomer(int status, String message, String accessToken, LocalDateTime expiredAt) {
        this.status = status;
        this.message = message;
        this.accessToken = accessToken;
        this.expiredAt = expiredAt;
    }
}
