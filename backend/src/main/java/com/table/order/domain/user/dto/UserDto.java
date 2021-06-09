package com.table.order.domain.user.dto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDto {
    private Long id;
    private String username;
    private String name;
    private String phoneNum;
    private String address;

    @Builder
    public UserDto(Long id, String username, String name, String phoneNum, String address) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.phoneNum = phoneNum;
        this.address = address;
    }
}
