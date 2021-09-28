package com.table.order.domain.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.table.order.domain.store.controller.StoreController;
import com.table.order.domain.store.dto.StoreDto;
import com.table.order.domain.store.dto.request.RequestEnrollStore;
import com.table.order.domain.store.dto.response.ResponseEnrollStore;
import com.table.order.domain.store.service.StoreService;
import com.table.order.domain.user.entity.UserRole;
import com.table.order.domain.user.service.SecurityService;
import com.table.order.global.security.exception.JwtAccessDeniedHandler;
import com.table.order.global.security.exception.JwtAuthenticationEntryPoint;
import com.table.order.global.security.provider.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.table.order.global.common.RoleToCollection.*;
import static com.table.order.global.common.code.ResultCode.RESULT_ENROLL_STORE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StoreController.class)
@AutoConfigureRestDocs
class StoreControllerTest {
    @MockBean
    private StoreService storeService;
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
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("식당 생성 테스트")
    public void createStore() throws Exception{
        //given
        StoreDto storeDto = StoreDto.builder()
                .id(1L)
                .name("식당")
                .description("식당 설명")
                .licenseImage("이미지 주소")
                .build();

        ResponseEnrollStore responseEnrollStore = ResponseEnrollStore.builder()
                .status(RESULT_ENROLL_STORE.getStatus())
                .message(RESULT_ENROLL_STORE.getMessage())
                .data(storeDto)
                .build();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("test", "1234", authorities(UserRole.USER));
        given(jwtProvider.getAuthentication(anyString())).willReturn(authentication);
        given(storeService.createStore(any(RequestEnrollStore.class), anyString())).willReturn(responseEnrollStore);

        //when
        RequestEnrollStore requestEnrollStore = RequestEnrollStore.builder()
                .name("식당")
                .description("식당 설명")
                .licenseImage("이미지 주소")
                .build();

        ResultActions result = mockMvc.perform(
                post("/stores").header("Authorization","Bearer (accessToken)")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestEnrollStore))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        //then
        result.andExpect(status().isCreated())
                .andDo(document("enrollStore",
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer + (로그인 요청 access 토큰)")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("식당명"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("설명"),
                                fieldWithPath("licenseImage").type(JsonFieldType.STRING).description("사업자등록증")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("식당 고유 id"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("식당명"),
                                fieldWithPath("data.description").type(JsonFieldType.STRING).description("설명"),
                                fieldWithPath("data.licenseImage").type(JsonFieldType.STRING).description("사업자등록증")
                        )
                ));
    }

    @Test
    @DisplayName("식당 조회 테스트")
    public void findStore() throws Exception{
        //given
        StoreDto storeDto = StoreDto.builder()
                .id(1L)
                .name("식당")
                .description("식당 설명")
                .licenseImage("이미지 주소")
                .build();

        ResponseEnrollStore responseEnrollStore = ResponseEnrollStore.builder()
                .status(RESULT_ENROLL_STORE.getStatus())
                .message(RESULT_ENROLL_STORE.getMessage())
                .data(storeDto)
                .build();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("test", "1234", authorities(UserRole.USER));
        given(jwtProvider.getAuthentication(anyString())).willReturn(authentication);
        given(storeService.findStore(anyString())).willReturn(responseEnrollStore);

        //when
        ResultActions result = mockMvc.perform(
                get("/stores").header("Authorization","Bearer (accessToken)")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        //then
        result.andExpect(status().isOk())
                .andDo(document("findStore",
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer + (로그인 요청 access 토큰)")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("식당 고유 id"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("식당명"),
                                fieldWithPath("data.description").type(JsonFieldType.STRING).description("설명"),
                                fieldWithPath("data.licenseImage").type(JsonFieldType.STRING).description("사업자등록증")
                        )
                ));
    }
}