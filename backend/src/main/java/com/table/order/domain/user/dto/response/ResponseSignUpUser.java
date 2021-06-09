package com.table.order.domain.user.dto.response;

import com.table.order.domain.user.dto.UserDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResponseSignUpUser {
    private int status;
    private String message;
    private UserDto data;

    @Builder
    public ResponseSignUpUser(int status, String message, UserDto data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
