package com.table.order.domain.user;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.table.order.domain.user.controller.UserController;
import com.table.order.domain.user.dto.UserDto;
import com.table.order.domain.user.dto.request.RequestLoginUser;
import com.table.order.domain.user.dto.request.RequestSignUpUser;
import com.table.order.domain.user.dto.response.ResponseLoginUser;
import com.table.order.domain.user.dto.response.ResponseSignUpUser;
import com.table.order.domain.user.entity.UserRole;
import com.table.order.domain.user.service.SecurityService;
import com.table.order.domain.user.service.UserService;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import java.time.LocalDateTime;

import static com.table.order.global.common.RoleToCollection.authorities;
import static com.table.order.global.common.code.ResultCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureRestDocs
class UserControllerTest {
    @MockBean
    private UserService userService;
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
    @DisplayName("user ?????? ?????? ?????????")
    public void signUpUser() throws Exception{
        //given
        UserDto dto = UserDto.builder()
                .id(1L)
                .username("beomjun")
                .name("?????????")
                .phoneNum("01012345678")
                .address("????????? ????????? 0000")
                .build();

        ResponseSignUpUser responseSignUpUser = ResponseSignUpUser.builder()
                .status(RESULT_SIGNUP_USER.getStatus())
                .message(RESULT_SIGNUP_USER.getMessage())
                .data(dto)
                .build();

        given(userService.signUpUser(any(RequestSignUpUser.class)))
                .willReturn(responseSignUpUser);
        //when
        RequestSignUpUser requestSignUpUser = RequestSignUpUser.builder()
                .username("beomjun")
                .password("1234")
                .name("?????????")
                .phoneNum("01012345678")
                .address("????????? ????????? 0000")
                .build();

        ResultActions result = mockMvc.perform(
                post("/users")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestSignUpUser))
                .accept(APPLICATION_JSON))
                .andDo(print());
        //then
        result.andExpect(status().isCreated())
                .andDo(document("signUpUser",
                        requestFields(
                                fieldWithPath("username").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("????????????"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("phoneNum").type(JsonFieldType.STRING).description("????????????"),
                                fieldWithPath("address").type(JsonFieldType.STRING).description("??????")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("?????? ?????????"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("?????? ?????? id"),
                                fieldWithPath("data.username").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("data.phoneNum").type(JsonFieldType.STRING).description("????????????"),
                                fieldWithPath("data.address").type(JsonFieldType.STRING).description("??????")
                        )
                ));
    }

    @Test
    @DisplayName("????????? ?????????")
    public void login() throws Exception{
        //given
        ResponseLoginUser responseLoginUser = ResponseLoginUser.builder()
                .status(RESULT_LOGIN.getStatus())
                .message(RESULT_LOGIN.getMessage())
                .accessToken("(accessToken)")
                .expiredAt(LocalDateTime.now().plusSeconds(1800))
                .refreshToken("(refreshToken)")
                .build();

        given(userService.login(any(RequestLoginUser.class)))
                .willReturn(responseLoginUser);

        //when
        RequestLoginUser requestLoginUser = RequestLoginUser.builder()
                .username("beomjun")
                .password("1234")
                .build();

        ResultActions result = mockMvc.perform(
                post("/users/login")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestLoginUser))
                .accept(APPLICATION_JSON))
                .andDo(print());

        //then
        result.andExpect(status().isOk())
                .andDo(document("loginUser",
                        requestFields(
                                fieldWithPath("username").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("????????????")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("?????? ?????????"),
                                fieldWithPath("accessToken").type(JsonFieldType.STRING).description("access ??????"),
                                fieldWithPath("expiredAt").type(JsonFieldType.STRING).description("?????? ?????? ??????"),
                                fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("refresh ??????")
                        )
                ));
    }

    @Test
    @DisplayName("?????? ?????? ?????????")
    public void findUser() throws Exception{
        //given
        UserDto dto = UserDto.builder()
                .id(1L)
                .username("beomjun")
                .name("?????????")
                .phoneNum("01012345678")
                .address("????????? ????????? 0000")
                .build();

        ResponseSignUpUser responseSignUpUser = ResponseSignUpUser.builder()
                .status(RESULT_FIND_USER.getStatus())
                .message(RESULT_FIND_USER.getMessage())
                .data(dto)
                .build();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("test", "1234", authorities(UserRole.USER));
        given(jwtProvider.getAuthentication(anyString())).willReturn(authentication);
        given(userService.findUser(any()))
                .willReturn(responseSignUpUser);
        //when
        ResultActions result = mockMvc.perform(
                get("/users").header("Authorization","Bearer (accessToken)")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andDo(print());
        //then
        result.andExpect(status().isOk())
                .andDo(document("findUser",
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer + (????????? ?????? access ??????)")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("?????? ?????????"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("?????? ?????? id"),
                                fieldWithPath("data.username").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("data.phoneNum").type(JsonFieldType.STRING).description("????????????"),
                                fieldWithPath("data.address").type(JsonFieldType.STRING).description("??????")
                        )
                ));
    }
}