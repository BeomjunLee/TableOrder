package com.table.order.global.security.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.table.order.global.common.ResponseError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.table.order.global.common.code.CustomErrorCode.ERROR_ACCESS_DENIED;

@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        sendErrorResponse(response, ERROR_ACCESS_DENIED.getMessage());
    }

    /**
     * jwt 예외처리 응답
     * @param message 예외 메세지
     */
    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(ResponseError.builder()
                .errorCode(ERROR_ACCESS_DENIED.getErrorCode())
                .status(ERROR_ACCESS_DENIED.getStatus())
                .message(message)
                .build()));
    }
}
