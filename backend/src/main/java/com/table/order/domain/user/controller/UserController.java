package com.table.order.domain.user.controller;

import com.table.order.domain.user.dto.request.RequestLoginUser;
import com.table.order.domain.user.dto.request.RequestSignUpUser;
import com.table.order.domain.user.dto.response.ResponseLoginUser;
import com.table.order.domain.user.dto.response.ResponseSignUpUser;
import com.table.order.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseLoginUser login(@RequestBody RequestLoginUser userLogin) {
        return userService.login(userLogin);
    }

    @PostMapping("")
    @ResponseStatus(CREATED)
    public ResponseSignUpUser signUpUser(@RequestBody RequestSignUpUser signUpUser) {
        return userService.signUpUser(signUpUser);
    }

    @GetMapping("")
    public ResponseSignUpUser findUser(Authentication authentication) {
        return userService.findUser(authentication.getName());
    }
}
