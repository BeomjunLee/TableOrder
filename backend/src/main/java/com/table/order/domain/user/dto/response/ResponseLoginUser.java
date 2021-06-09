package com.table.order.domain.user.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResponseLoginUser {
    private int status;
    private String message;
    private String accessToken;
    private LocalDateTime expiredAt;
    private String refreshToken;

    @Builder
    public ResponseLoginUser(int status, String message, String accessToken, LocalDateTime expiredAt, String refreshToken) {
        this.status = status;
        this.message = message;
        this.accessToken = accessToken;
        this.expiredAt = expiredAt;
        this.refreshToken = refreshToken;
    }
}
