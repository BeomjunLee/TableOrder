package com.table.order.global.common.code;

import lombok.Getter;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum CustomErrorCode {
    ERROR_NOT_FOUND_TOKEN(0, UNAUTHORIZED.value(), "토큰을 찾을 수 없습니다"),
    ERROR_ACCESS_DENIED(1, FORBIDDEN.value(), "권한이 없습니다"),
    ERROR_LOGIN_FAIL(2, UNAUTHORIZED.value(), "로그인 실패"),
    ERROR_NOT_FOUND_TABLE_STORE(3, BAD_REQUEST.value(), "테이블 또는 식당을 찾을 수 없습니다"),
    ERROR_NOT_FOUND_USER(4, BAD_REQUEST.value(), "회원을 찾을 수 없습니다"),
    ERROR_NOT_FOUND_USER_TABLE(5, BAD_REQUEST.value(), "회원 또는 테이블을 찾을 수 없습니다"),
    ERROR_NOT_FOUND_STORE(6, BAD_REQUEST.value(), "식당을 찾을 수 없습니다"),
    ERROR_NOT_FOUND_CATEGORY(7, BAD_REQUEST.value(), "카테고리를 찾을 수 없습니다"),
    ERROR_IN_USE_TABLE(8, FORBIDDEN.value(), "사용중인 테이블입니다"),
    ERROR_INVALID_STORE(9, FORBIDDEN.value(), "승인되지 않은 매장입니다"),
    ERROR_DUPLICATE_USERNAME(10, BAD_REQUEST.value(), "중복된 아이디입니다")
    ;

    private final int errorCode;
    private final int status;
    private final String message;

    CustomErrorCode(int errorCode, int status, String message) {
        this.errorCode = errorCode;
        this.status = status;
        this.message = message;
    }
}
