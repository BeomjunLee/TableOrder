package com.table.order.domain.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.table.order.domain.customer.controller.CustomerController;
import com.table.order.domain.customer.dto.request.RequestLoginCustomer;
import com.table.order.domain.customer.dto.response.ResponseLoginCustomer;
import com.table.order.domain.customer.service.CustomerService;
import com.table.order.domain.user.service.SecurityService;
import com.table.order.global.common.response.ResponseResult;
import com.table.order.global.security.exception.JwtAccessDeniedHandler;
import com.table.order.global.security.exception.JwtAuthenticationEntryPoint;
import com.table.order.global.security.provider.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import java.time.LocalDateTime;

import static com.table.order.global.common.code.ResultCode.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
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
    @MockBean
    private SecurityService securityService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("QR ?????? ?????? ????????? (????????????)")
    public void scanQrCode() throws Exception{
        //given
        ResponseLoginCustomer responseLoginCustomer = ResponseLoginCustomer.builder()
                .status(RESULT_SIGN_UP_CUSTOMER.getStatus())
                .message(RESULT_SIGN_UP_CUSTOMER.getMessage())
                .accessToken("(accessToken)")
                .expiredAt(LocalDateTime.now().plusSeconds(1800))
                .build();

        given(customerService.scanQrCode(any(RequestLoginCustomer.class)))
                .willReturn(responseLoginCustomer);

        //when
        RequestLoginCustomer requestLoginCustomer = RequestLoginCustomer.builder()
                .username("7c982859267771f3")
                .tableId(1L)
                .storeId(2L)
                .build();

        ResultActions result = mockMvc.perform(
                post("/app/customers")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestLoginCustomer))
                        .accept(APPLICATION_JSON))
                        .andDo(print());

        //then
        result.andExpect(status().isCreated())
                .andDo(document("scanQrCode",
                        requestFields(
                            fieldWithPath("username").type(JsonFieldType.STRING).description("????????? ?????? ?????? (?????????)"),
                            fieldWithPath("tableId").type(JsonFieldType.NUMBER).description("????????? ?????? id"),
                            fieldWithPath("storeId").type(JsonFieldType.NUMBER).description("?????? ?????? id")
                        ),
                        responseFields(
                            fieldWithPath("status").type(JsonFieldType.NUMBER).description("?????? ??????"),
                            fieldWithPath("message").type(JsonFieldType.STRING).description("?????? ?????????"),
                            fieldWithPath("accessToken").type(JsonFieldType.STRING).description("access ??????"),
                            fieldWithPath("expiredAt").type(JsonFieldType.STRING).description("?????? ?????? ??????")
                        )
                ));
    }

    @Test
    @DisplayName("QR ?????? ?????? ????????? (????????????)")
    public void scanQrCodeReLogin() throws Exception{
        //given
        ResponseLoginCustomer responseLoginCustomer = ResponseLoginCustomer.builder()
                .status(RESULT_RE_LOGIN.getStatus())
                .message(RESULT_RE_LOGIN.getMessage())
                .accessToken("(accessToken)")
                .expiredAt(LocalDateTime.now().plusSeconds(1800))
                .build();

        given(customerService.scanQrCode(any(RequestLoginCustomer.class)))
                .willReturn(responseLoginCustomer);

        //when
        RequestLoginCustomer requestLoginCustomer = RequestLoginCustomer.builder()
                .username("7c982859267771f3")
                .tableId(1L)
                .storeId(2L)
                .build();

        ResultActions result = mockMvc.perform(
                post("/app/customers")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestLoginCustomer))
                        .accept(APPLICATION_JSON))
                .andDo(print());

        //then
        result.andExpect(status().isOk())
                .andDo(document("scanQrCodeReLogin",
                        requestFields(
                                fieldWithPath("username").type(JsonFieldType.STRING).description("????????? ?????? ?????? (?????????)"),
                                fieldWithPath("tableId").type(JsonFieldType.NUMBER).description("????????? ?????? id"),
                                fieldWithPath("storeId").type(JsonFieldType.NUMBER).description("?????? ?????? id")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("?????? ?????????"),
                                fieldWithPath("accessToken").type(JsonFieldType.STRING).description("access ??????"),
                                fieldWithPath("expiredAt").type(JsonFieldType.STRING).description("?????? ?????? ??????")
                        )
                ));
    }

    @Test
    @DisplayName("QR ?????? ?????? ????????? (?????????)")
    public void scanQrCodeReVisit() throws Exception{
        //given
        ResponseLoginCustomer responseLoginCustomer = ResponseLoginCustomer.builder()
                .status(RESULT_RE_VISIT.getStatus())
                .message(RESULT_RE_VISIT.getMessage())
                .accessToken("(accessToken)")
                .expiredAt(LocalDateTime.now().plusSeconds(1800))
                .build();

        given(customerService.scanQrCode(any(RequestLoginCustomer.class)))
                .willReturn(responseLoginCustomer);

        //when
        RequestLoginCustomer requestLoginCustomer = RequestLoginCustomer.builder()
                .username("7c982859267771f3")
                .tableId(1L)
                .storeId(2L)
                .build();

        ResultActions result = mockMvc.perform(
                post("/app/customers")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestLoginCustomer))
                        .accept(APPLICATION_JSON))
                .andDo(print());

        //then
        result.andExpect(status().isOk())
                .andDo(document("scanQrCodeReVisit",
                        requestFields(
                                fieldWithPath("username").type(JsonFieldType.STRING).description("????????? ?????? ?????? (?????????)"),
                                fieldWithPath("tableId").type(JsonFieldType.NUMBER).description("????????? ?????? id"),
                                fieldWithPath("storeId").type(JsonFieldType.NUMBER).description("?????? ?????? id")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("?????? ?????????"),
                                fieldWithPath("accessToken").type(JsonFieldType.STRING).description("access ??????"),
                                fieldWithPath("expiredAt").type(JsonFieldType.STRING).description("?????? ?????? ??????")
                        )
                ));
    }

    @Test
    @DisplayName("?????????")
    public void test() throws Exception{
        //given

        ResponseResult responseResult = ResponseResult.builder()
                .status(200)
                .message("????????? ??????")
                .build();

        //when
        ResultActions result = mockMvc.perform(
                get("/app/customers/test")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andDo(print());
        //then
        result.andExpect(status().isOk())
                .andDo(document("test",
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("?????? ?????????")
                        )
                ));
    }
}