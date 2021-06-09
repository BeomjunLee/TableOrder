package com.table.order.domain.user.service;

import com.table.order.domain.user.entity.User;
import com.table.order.domain.user.entity.UserRole;
import com.table.order.domain.user.repository.UserRepository;
import com.table.order.global.common.exception.CustomIllegalArgumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

import static com.table.order.global.common.code.CustomErrorCode.ERROR_NOT_FOUND_USER;

@Service
@RequiredArgsConstructor
public class SecurityService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * security 로그인 처리 메서드
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomIllegalArgumentException(ERROR_NOT_FOUND_USER.getErrorCode(), ERROR_NOT_FOUND_USER.getMessage()));

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities(user.getUserRole()));
    }

    private Collection<? extends GrantedAuthority> authorities(UserRole userRole) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(userRole.getRoleName()));
        return authorities;
    }
}
