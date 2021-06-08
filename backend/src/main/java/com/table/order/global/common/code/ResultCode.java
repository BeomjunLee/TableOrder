package com.table.order.global.common.code;

import lombok.Getter;

@Getter
public enum ResultCode {

    SIGN_UP(201, "회원가입 성공"),
    RE_LOGIN(200, "재로그인 성공");

    private final int status;
    private final String message;

    ResultCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
