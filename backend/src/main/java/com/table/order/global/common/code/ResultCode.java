package com.table.order.global.common.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Getter
public enum ResultCode {

    RESULT_SIGN_UP_CUSTOMER(CREATED.value(), "회원가입 성공"),  //가입 후 바로 accessToken 발급해야해서 (201 대신 200 적용)
    RESULT_RE_LOGIN(OK.value(), "재로그인 성공"),
    RESULT_LOGIN(OK.value(), "로그인 성공"),
    RESULT_RE_VISIT(OK.value(), "식당 재방문 성공"),
    RESULT_SIGNUP_USER(CREATED.value(), "회원 가입 성공"),
    RESULT_ENROLL_STORE(CREATED.value(), "식당 등록 성공"),
    RESULT_ADD_TABLE(CREATED.value(), "테이블 추가 성공"),
    RESULT_ADD_CATEGORY(CREATED.value(), "카테고리 추가 성공"),
    RESULT_ADD_ITEM(CREATED.value(), "메뉴 추가 성공"),
    RESULT_CREATE_ORDER(CREATED.value(), "주문 생성 성공"),
    RESULT_FIND_TABLES(OK.value(), "테이블 검색 성공"),
    RESULT_FIND_CATEGORIES_ITEMS(OK.value(), "카테고리, 메뉴 검색 성공")
    ;

    private final int status;
    private final String message;

    ResultCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
