package com.table.order.global.common.code;

import lombok.Getter;

@Getter
public enum CustomErrorCode {
    ERROR_ACCESS_DENIED(01, 403, "권한이 없습니다"),
    ERROR_LOGIN_FAIL(02, 401, "로그인 실패"),
    ERROR_NOT_FOUND_TABLE_STORE(03, 401, "테이블 또는 식당을 찾을 수 없습니다")
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
