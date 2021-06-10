package com.table.order.global.common;

import com.table.order.domain.user.entity.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

public class RoleToCollection {

    public static Collection<? extends GrantedAuthority> authorities(UserRole userRole) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(userRole.getRoleName()));
        return authorities;
    }
}
