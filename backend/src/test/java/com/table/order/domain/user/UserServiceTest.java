package com.table.order.domain.user;

import com.table.order.domain.user.dto.UserDto;
import com.table.order.domain.user.dto.request.RequestLoginUser;
import com.table.order.domain.user.dto.request.RequestSignUpUser;
import com.table.order.domain.user.dto.response.ResponseLoginUser;
import com.table.order.domain.user.dto.response.ResponseSignUpUser;
import com.table.order.domain.user.entity.User;
import com.table.order.domain.user.repository.UserRepository;
import com.table.order.domain.user.service.UserService;
import com.table.order.global.common.code.CustomErrorCode;
import com.table.order.global.common.exception.CustomIllegalArgumentException;
import com.table.order.global.security.provider.JwtProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.event.annotation.AfterTestClass;
import org.springframework.test.context.event.annotation.BeforeTestClass;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.table.order.global.common.code.CustomErrorCode.ERROR_DUPLICATE_USERNAME;
import static com.table.order.global.common.code.ResultCode.RESULT_LOGIN;
import static com.table.order.global.common.code.ResultCode.RESULT_SIGNUP_USER;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private PasswordEncoder passwordEncoder;

    private RequestSignUpUser requestSignUpUser;
    private ResponseSignUpUser responseSignUpUser;
    private RequestLoginUser requestLoginUser;
    private ResponseLoginUser responseLoginUser;
    private UserDto userDto;
    private User user;

    @BeforeEach
    public void init() {
        requestSignUpUser = RequestSignUpUser.builder()
                .username("beomjun")
                .password("1234")
                .name("이범준")
                .phoneNum("01012345678")
                .address("서울시 강남구 0000")
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .username("beomjun")
                .name("이범준")
                .phoneNum("01012345678")
                .address("서울시 강남구 0000")
                .build();

        responseSignUpUser = ResponseSignUpUser.builder()
                .status(RESULT_SIGNUP_USER.getStatus())
                .message(RESULT_SIGNUP_USER.getMessage())
                .data(userDto)
                .build();

        requestLoginUser = RequestLoginUser.builder()
                .username("test")
                .password("1234")
                .build();

        responseLoginUser = ResponseLoginUser.builder()
                .status(RESULT_LOGIN.getStatus())
                .message(RESULT_LOGIN.getMessage())
                .accessToken("(accessToken)")
                .expiredAt(LocalDateTime.now().plusSeconds(1800))
                .refreshToken("(refreshToken)")
                .build();

        user = User.createUser(requestSignUpUser, "encoded");

    }

    @Test
    @DisplayName("회원가입 테스트")
    public void signUpUser() throws Exception{
        //given
        given(userRepository.countByUsername(anyString())).willReturn(0L);
        given(userRepository.save(any(User.class))).willReturn(user);

        //when
        ResponseSignUpUser response = userService.signUpUser(requestSignUpUser);

        //then
        assertThat(response).extracting("status", "message", "data.username", "data.name", "data.phoneNum", "data.address")
                .containsExactly(
                        responseSignUpUser.getStatus(),
                        responseSignUpUser.getMessage(),
                        responseSignUpUser.getData().getUsername(),
                        responseSignUpUser.getData().getName(),
                        responseSignUpUser.getData().getPhoneNum(),
                        responseSignUpUser.getData().getAddress());
    }

    @Test
    @DisplayName("회원가입 실패 테스트 (아이디 중복)")
    public void signUpUserDuplicateUsername() throws Exception{
        //given
        given(userRepository.countByUsername(anyString())).willReturn(1L);

        //when then
        assertThatThrownBy(() -> {
            userService.signUpUser(requestSignUpUser);
        }).isInstanceOf(CustomIllegalArgumentException.class).hasMessageContaining(ERROR_DUPLICATE_USERNAME.getMessage());
    }

    @Test
    @DisplayName("로그인 테스트")
    public void login() throws Exception{
        //given
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(requestLoginUser.getUsername(), requestLoginUser.getPassword());

        given(authenticationManager.authenticate(token)).willReturn(token);
        given(jwtProvider.generateToken(token, false)).willReturn("(accessToken)");
        given(jwtProvider.generateToken(token, true)).willReturn("(refreshToken)");
        given(userRepository.findByUsername(any())).willReturn(Optional.ofNullable(user));

        //when
        ResponseLoginUser response = userService.login(requestLoginUser);

        //then
        assertThat(response).extracting("status", "message", "accessToken", "refreshToken")
                .containsExactly(responseLoginUser.getStatus(), responseLoginUser.getMessage(), responseLoginUser.getAccessToken(), responseLoginUser.getRefreshToken());
    }
}