package com.table.order.domain.user.service;
import com.table.order.domain.user.dto.UserDto;
import com.table.order.domain.user.dto.request.RequestLoginUser;
import com.table.order.domain.user.dto.request.RequestSignUpUser;
import com.table.order.domain.user.dto.response.ResponseLoginUser;
import com.table.order.domain.user.dto.response.ResponseSignUpUser;
import com.table.order.domain.user.entity.User;
import com.table.order.domain.user.repository.UserRepository;
import com.table.order.global.common.exception.CustomIllegalArgumentException;
import com.table.order.global.security.provider.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.table.order.global.common.code.CustomErrorCode.ERROR_DUPLICATE_USERNAME;
import static com.table.order.global.common.code.CustomErrorCode.ERROR_NOT_FOUND_USER;
import static com.table.order.global.common.code.ResultCode.RESULT_LOGIN;
import static com.table.order.global.common.code.ResultCode.RESULT_SIGNUP_USER;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService{

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    /**
     * 로그인
     * @param requestLoginUser 클라이언트 로그인 요청 dto
     * @return 응답 dto
     */
    public ResponseLoginUser login(RequestLoginUser requestLoginUser) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(requestLoginUser.getUsername(), requestLoginUser.getPassword());
        Authentication authentication = authenticationManager.authenticate(token);

        String accessToken = jwtProvider.generateToken(authentication, false);
        String refreshToken = jwtProvider.generateToken(authentication, true);

        saveRefreshToken(authentication.getName(), refreshToken);

        return ResponseLoginUser.builder()
                .status(RESULT_LOGIN.getStatus())
                .message(RESULT_LOGIN.getMessage())
                .accessToken(accessToken)
                .expiredAt(LocalDateTime.now().plusSeconds(jwtProvider.getAccessTokenValidMilliSeconds()/1000))
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * refreshToken 저장
     * @param username 회원 아이디
     * @param refreshToken 회원 refreshToken
     */
    private void saveRefreshToken(String username, String refreshToken) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomIllegalArgumentException(ERROR_NOT_FOUND_USER.getErrorCode(), ERROR_NOT_FOUND_USER.getMessage()));
        user.updateRefreshToken(refreshToken);
    }

    /**
     * 회원 가입
     * @param requestSignUpUser 클라이언트 회원가입 요청 dto
     * @return 응답 dto
     */
    public ResponseSignUpUser signUpUser(RequestSignUpUser requestSignUpUser) {

        validateDuplicateUser(requestSignUpUser.getUsername());

        User user = User.createUser(requestSignUpUser, passwordEncoder.encode(requestSignUpUser.getPassword()));
        User savedUser = userRepository.save(user);

        UserDto dto = UserDto.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .name(savedUser.getName())
                .phoneNum(savedUser.getPhoneNum())
                .address(savedUser.getAddress())
                .build();

        return ResponseSignUpUser.builder()
                .status(RESULT_SIGNUP_USER.getStatus())
                .message(RESULT_SIGNUP_USER.getMessage())
                .data(dto)
                .build();
    }

    private void validateDuplicateUser(String username) {
        if(userRepository.countByUsername(username) > 0)
            throw new CustomIllegalArgumentException(ERROR_DUPLICATE_USERNAME.getErrorCode(), ERROR_DUPLICATE_USERNAME.getMessage());
    }


}
