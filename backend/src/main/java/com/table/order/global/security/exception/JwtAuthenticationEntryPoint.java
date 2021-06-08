package com.table.order.global.security.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.table.order.global.common.ResponseError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.table.order.global.common.code.CustomErrorCode.ERROR_LOGIN_FAIL;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        sendErrorResponse(response, ERROR_LOGIN_FAIL.getMessage());
    }

    /**
     * jwt 예외처리 응답
     * @param message 예외 메세지
     */
    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(ResponseError.builder()
                .errorCode(ERROR_LOGIN_FAIL.getErrorCode())
                .status(ERROR_LOGIN_FAIL.getStatus())
                .message(message)
                .build()));
    }
}
