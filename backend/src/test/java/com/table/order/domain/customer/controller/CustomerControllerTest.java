package com.table.order.domain.customer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.table.order.domain.customer.dto.request.RequestCustomerLogin;
import com.table.order.domain.customer.dto.response.ResponseLogin;
import com.table.order.domain.customer.repository.CustomerRepository;
import com.table.order.domain.customer.service.CustomerService;
import com.table.order.domain.table.repository.TableQueryRepository;
import com.table.order.global.common.code.ResultCode;
import com.table.order.global.security.exception.JwtAccessDeniedHandler;
import com.table.order.global.security.exception.JwtAuthenticationEntryPoint;
import com.table.order.global.security.provider.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;

import static com.table.order.global.common.code.ResultCode.SIGN_UP;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
@AutoConfigureRestDocs
class CustomerControllerTest {

    @MockBean
    private CustomerService customerService;
    @MockBean
    private JwtProvider jwtProvider;
    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @MockBean
    private JwtAccessDeniedHandler jwtAccessDeniedHandler;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("QR 코드 스캔 테스트")
    public void scanQrCode() throws Exception{
        //given
        ResponseLogin responseLogin = ResponseLogin.builder()
                .status(HttpStatus.OK.value())
                .message(SIGN_UP.getMessage())
                .accessToken("accessToken")
                .expiredAt(LocalDateTime.now().plusSeconds(100))
                .build();

        given(customerService.scanQrCode(any(RequestCustomerLogin.class)))
                .willReturn(responseLogin);

        //when
        RequestCustomerLogin requestCustomerLogin = RequestCustomerLogin.builder()
                .username("test")
                .tableId(1L)
                .storeId(2L)
                .build();

        ResultActions result = mockMvc.perform(
                post("/customers/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCustomerLogin))
                        .accept(APPLICATION_JSON))
                        .andDo(print());

        //then
        result.andExpect(status().isOk())
                .andDo(document("scanQrCode",
                        requestFields(
                            fieldWithPath("username").type(JsonFieldType.STRING).description("핸드폰 고유 번호 (아이디)"),
                            fieldWithPath("tableId").type(JsonFieldType.NUMBER).description("테이블 고유 id"),
                            fieldWithPath("storeId").type(JsonFieldType.NUMBER).description("매장 고유 id")
                        ),
                        responseFields(
                            fieldWithPath("status").type(JsonFieldType.NUMBER).description("상태 코드"),
                            fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                            fieldWithPath("accessToken").type(JsonFieldType.STRING).description("access 토큰"),
                            fieldWithPath("expiredAt").type(JsonFieldType.STRING).description("토큰 만료 시간")
                        )
                ));


    }
}