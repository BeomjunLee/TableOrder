package com.table.order.domain.user.entity;

import com.table.order.domain.BaseEntity;
import com.table.order.domain.user.dto.request.RequestSignUpUser;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@javax.persistence.Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phoneNum;

    @Column(nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    private String refreshToken;

    @Builder
    public User(String username, String password, String name, String phoneNum, String address, UserRole userRole) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.phoneNum = phoneNum;
        this.address = address;
        this.userRole = userRole;
    }

    public static User createUser(RequestSignUpUser requestSignUpUser, String encodedPassword) {
        User user = User.builder()
                .username(requestSignUpUser.getUsername())
                .password(encodedPassword)
                .name(requestSignUpUser.getName())
                .phoneNum(requestSignUpUser.getPhoneNum())
                .address(requestSignUpUser.getAddress())
                .userRole(UserRole.USER)
                .build();

        return user;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}
