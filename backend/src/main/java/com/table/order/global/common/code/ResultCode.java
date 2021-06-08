package com.table.order.global.common.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Getter
public enum ResultCode {

    RESULT_SIGN_UP(OK.value(), "회원가입 성공"),  //가입 후 바로 accessToken 발급해야해서 (201 대신 200 적용)
    RESULT_RE_LOGIN(OK.value(), "재로그인 성공"),
    RESULT_ENROLL_STORE(CREATED.value(), "식당 생성 성공")
    ;

    private final int status;
    private final String message;

    ResultCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
