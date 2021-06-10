package com.table.order.domain.table;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.table.order.domain.store.dto.request.RequestEnrollStore;
import com.table.order.domain.store.entity.Store;
import com.table.order.domain.store.entity.StoreStatus;
import com.table.order.domain.table.controller.TableController;
import com.table.order.domain.table.dto.TableDto;
import com.table.order.domain.table.dto.request.RequestAddTable;
import com.table.order.domain.table.dto.response.ResponseAddTable;
import com.table.order.domain.table.entity.TableStatus;
import com.table.order.domain.table.service.TableService;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.Collection;

import static com.table.order.global.common.RoleToCollection.authorities;
import static com.table.order.global.common.code.ResultCode.RESULT_ADD_TABLE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TableController.class)
@AutoConfigureRestDocs
class TableControllerTest {
    @MockBean
    private TableService tableService;
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
    @DisplayName("테이블 추가 테스트")
    public void addTable() throws Exception{
        //given
        TableDto tableDto = TableDto.builder()
                .id(1L)
                .name("테이블 1")
                .numberOfPeople(5)
                .tableStatus(TableStatus.OPEN)
                .build();

        ResponseAddTable responseAddTable = ResponseAddTable.builder()
                .status(RESULT_ADD_TABLE.getStatus())
                .message(RESULT_ADD_TABLE.getMessage())
                .data(tableDto)
                .build();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("test", "1234", authorities(UserRole.USER));
        given(jwtProvider.getAuthentication(anyString())).willReturn(authentication);
        given(tableService.addTable(any(RequestAddTable.class), anyString())).willReturn(responseAddTable);

        //when
        RequestAddTable requestAddTable = RequestAddTable.builder()
                .name("테이블 1")
                .numberOfPeople(5)
                .build();

        ResultActions result = mockMvc.perform(
                post("/tables").header("Authorization","Bearer (accessToken)")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestAddTable))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        //then
        result.andExpect(status().isCreated())
                .andDo(document("addTable",
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer + (로그인 요청 access 토큰)")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("테이블명"),
                                fieldWithPath("numberOfPeople").type(JsonFieldType.NUMBER).description("테이블 좌석 수")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("테이블 고유 id"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("테이블명"),
                                fieldWithPath("data.numberOfPeople").type(JsonFieldType.NUMBER).description("테이블 좌석 수"),
                                fieldWithPath("data.tableStatus").type(JsonFieldType.STRING).description("테이블 상태")
                        )
                ));
    }
}
