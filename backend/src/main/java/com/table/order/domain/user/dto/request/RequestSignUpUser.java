package com.table.order.domain.user.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestSignUpUser {
    private String username;
    private String password;
    private String name;
    private String phoneNum;
    private String address;

    @Builder
    public RequestSignUpUser(String username, String password, String name, String phoneNum, String address) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.phoneNum = phoneNum;
        this.address = address;
    }
}
