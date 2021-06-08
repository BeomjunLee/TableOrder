package com.table.order.domain.customer.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ResponseLogin {
    private int status;
    private String message;
    private String accessToken;
    private int expiredAt;

    @Builder
    public ResponseLogin(int status, String message, String accessToken, int expiredAt) {
        this.status = status;
        this.message = message;
        this.accessToken = accessToken;
        this.expiredAt = expiredAt;
    }
}
