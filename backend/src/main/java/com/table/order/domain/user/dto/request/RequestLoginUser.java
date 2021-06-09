package com.table.order.domain.user.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestLoginUser {
    private String username;
    private String password;

    @Builder
    public RequestLoginUser(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
