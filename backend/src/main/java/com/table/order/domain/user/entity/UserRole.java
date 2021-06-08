package com.table.order.domain.user.entity;

import lombok.Getter;

@Getter
public enum UserRole {
    BEFORE("ROLE_BEFORE"), USER("ROLE_USER"), ADMIN("ROLE_ADMIN");

    private String role;

    UserRole(String role) {
        this.role = role;
    }
}

