package com.table.order.domain.table;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.table.order.domain.item.dto.request.RequestUpdateItem;
import com.table.order.domain.order.dto.OrderDto;
import com.table.order.domain.order.entity.OrderStatus;
import com.table.order.domain.table.controller.TableController;
import com.table.order.domain.table.dto.TableDto;
import com.table.order.domain.table.dto.request.RequestAddTable;
import com.table.order.domain.table.dto.request.RequestUpdateTable;
import com.table.order.domain.table.dto.response.ResponseAddTable;
import com.table.order.domain.table.dto.response.ResponseTables;
import com.table.order.domain.table.entity.TableStatus;
import com.table.order.domain.table.service.TableService;
import com.table.order.domain.user.entity.UserRole;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static com.table.order.global.common.RoleToCollection.authorities;
import static com.table.order.global.common.code.ResultCode.*;
import static com.table.order.global.common.code.ResultCode.RESULT_UPDATE_ITEM;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
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
    @DisplayName("????????? ?????? ?????????")
    public void addTable() throws Exception{
        //given
        TableDto tableDto = TableDto.builder()
                .id(1L)
                .name("????????? 1")
                .numberOfPeople(5)
                .totalPrice(10000)
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
                .name("????????? 1")
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
                                headerWithName("Authorization").description("Bearer + (????????? ?????? access ??????)")
                        ),
                        requestFields(
                                fieldWithPath("name").type(STRING).description("????????????"),
                                fieldWithPath("numberOfPeople").type(NUMBER).description("????????? ?????? ???")
                        ),
                        responseFields(
                                fieldWithPath("status").type(NUMBER).description("?????? ??????"),
                                fieldWithPath("message").type(STRING).description("?????? ?????????"),
                                fieldWithPath("data.id").type(NUMBER).description("????????? ?????? id"),
                                fieldWithPath("data.name").type(STRING).description("????????????"),
                                fieldWithPath("data.numberOfPeople").type(NUMBER).description("????????? ?????? ???"),
                                fieldWithPath("data.totalPrice").type(NUMBER).description("????????? ??? ?????? ??????"),
                                fieldWithPath("data.tableStatus").type(STRING).description("????????? ??????"),
                                fieldWithPath("data.orders").type(JsonFieldType.NULL).description("????????? ?????? ??????")
                        )
                ));
    }
    
    @Test
    @DisplayName("????????? ?????? ?????? ?????????")
    public void findTables() throws Exception{
        //given
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("test", "1234", authorities(UserRole.USER));
        given(jwtProvider.getAuthentication(anyString())).willReturn(authentication);

        List<OrderDto> orders = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            OrderDto orderDto = OrderDto.builder()
                    .id((long) i)
                    .name("??????"+i)
                    .orderPrice(i * 5000)
                    .count(i)
                    .request("?????????????????????")
                    .orderStatus(OrderStatus.ORDER)
                    .build();
            orders.add(orderDto);
        }

        List<TableDto> tables = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            TableDto tableDto = TableDto.builder()
                    .id(1L)
                    .name("????????? 1")
                    .numberOfPeople(5)
                    .totalPrice(15000)
                    .tableStatus(TableStatus.OPEN)
                    .orders(orders)
                    .build();
            tables.add(tableDto);
        }

        Pageable pageable = PageRequest.of(0, 10);

        Page<TableDto> pageResult = new PageImpl<TableDto>(tables, pageable, 2);

        ResponseTables responseTables = ResponseTables.builder()
                .status(RESULT_FIND_TABLES.getStatus())
                .message(RESULT_FIND_TABLES.getMessage())
                .data(pageResult)
                .build();

        given(tableService.findTables(anyString(), any(Pageable.class))).willReturn(responseTables);

        //when
        ResultActions result = mockMvc.perform(
                get("/tables").header("Authorization","Bearer (accessToken)")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("page", "0")
                        .param("size", "10"))
                        .andDo(print());

        //then
        result.andExpect(status().isOk())
                .andDo(document("findTables",
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer + (????????? ?????? access ??????)")
                        ),
                        requestParameters(
                                parameterWithName("page").description("?????? ?????????"),
                                parameterWithName("size").description("??? ???????????? ????????? ????????? ??????(???????????? default ???)")
                        ),
                        responseFields(
                                fieldWithPath("status").type(NUMBER).description("?????? ??????"),
                                fieldWithPath("message").type(STRING).description("?????? ?????????"),
                                fieldWithPath("data.content.[].id").type(NUMBER).description("????????? ?????? id"),
                                fieldWithPath("data.content.[].name").type(STRING).description("????????????"),
                                fieldWithPath("data.content.[].numberOfPeople").type(NUMBER).description("????????? ?????? ???"),
                                fieldWithPath("data.content.[].totalPrice").type(NUMBER).description("????????? ??? ?????? ??????"),
                                fieldWithPath("data.content.[].tableStatus").type(STRING).description("????????? ??????"),

                                fieldWithPath("data.content.[].orders.[].id").type(NUMBER).description("?????? ?????? id"),
                                fieldWithPath("data.content.[].orders.[].name").type(STRING).description("?????? ??????"),
                                fieldWithPath("data.content.[].orders.[].orderPrice").type(NUMBER).description("?????? ??????"),
                                fieldWithPath("data.content.[].orders.[].count").type(NUMBER).description("?????? ??????"),
                                fieldWithPath("data.content.[].orders.[].request").type(STRING).description("?????? ????????????"),
                                fieldWithPath("data.content.[].orders.[].orderStatus").type(STRING).description("?????? ??????"),

                                fieldWithPath("data.pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("?????? ????????? ??????"),
                                fieldWithPath("data.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("?????? ???????????? ??????"),
                                fieldWithPath("data.pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("???????????? ???????????? ??????"),

                                fieldWithPath("data.pageable.pageNumber").type(NUMBER).description("?????? ????????? ??????"),
                                fieldWithPath("data.pageable.pageSize").type(NUMBER).description("??? ???????????? ????????? ????????? ??????"),
                                fieldWithPath("data.pageable.offset").type(NUMBER).description("????????? ??????????????? (0?????? ??????)"),
                                fieldWithPath("data.pageable.paged").type(JsonFieldType.BOOLEAN).description("????????? ????????? ??????????????? ??????"),
                                fieldWithPath("data.pageable.unpaged").type(JsonFieldType.BOOLEAN).description("????????? ????????? ?????????????????? ??????"),

                                fieldWithPath("data.last").type(JsonFieldType.BOOLEAN).description("????????? ??????????????? ??????"),
                                fieldWithPath("data.totalPages").type(NUMBER).description("?????? ????????? ??????"),
                                fieldWithPath("data.totalElements").type(NUMBER).description("????????? ??? ????????? ??????"),
                                fieldWithPath("data.first").type(JsonFieldType.BOOLEAN).description("????????? ??????????????? ??????"),
                                fieldWithPath("data.numberOfElements").type(NUMBER).description("?????? ??????????????? ?????? ??? ????????? ??????"),
                                fieldWithPath("data.number").type(NUMBER).description("?????? ????????? ??????"),
                                fieldWithPath("data.size").type(NUMBER).description("??? ???????????? ????????? ????????? ??????"),

                                fieldWithPath("data.sort.sorted").type(JsonFieldType.BOOLEAN).description("?????? ????????? ??????"),
                                fieldWithPath("data.sort.unsorted").type(JsonFieldType.BOOLEAN).description("?????? ???????????? ??????"),
                                fieldWithPath("data.sort.empty").type(JsonFieldType.BOOLEAN).description("???????????? ???????????? ??????"),

                                fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN).description("???????????? ???????????? ??????")
                        )
                ));
    }

    @Test
    @DisplayName("????????? ?????? ?????????")
    public void updateTable() throws Exception{
        //given
        ResponseResult responseResult = ResponseResult.builder()
                .status(RESULT_UPDATE_TABLE.getStatus())
                .message(RESULT_UPDATE_TABLE.getMessage())
                .build();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("test", "1234", authorities(UserRole.USER));
        given(jwtProvider.getAuthentication(anyString())).willReturn(authentication);
        given(tableService.updateTable(anyLong(), anyString(), any(RequestUpdateTable.class))).willReturn(responseResult);
        //when
        RequestUpdateTable requestUpdateTable = RequestUpdateTable.builder()
                .name("????????? ????????????")
                .numberOfPeople(5)
                .build();

        ResultActions result = mockMvc.perform(
                put("/tables/{tableId}", 1L).header("Authorization","Bearer (accessToken)")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUpdateTable)))
                .andDo(print());

        //then
        result.andExpect(status().isOk())
                .andDo(document("updateTable",
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer + (????????? ?????? access ??????)")
                        ),
                        pathParameters(
                                parameterWithName("tableId").description("????????? ?????? id")
                        ),
                        requestFields(
                                fieldWithPath("name").type(STRING).description("????????????"),
                                fieldWithPath("numberOfPeople").type(NUMBER).description("????????? ?????? ???")
                        ),
                        responseFields(
                                fieldWithPath("status").type(NUMBER).description("?????? ??????"),
                                fieldWithPath("message").type(STRING).description("?????? ?????????")
                        )
                ));
    }

    @Test
    @DisplayName("????????? ?????? ?????????")
    public void deleteTable() throws Exception{
        //given
        ResponseResult responseResult = ResponseResult.builder()
                .status(RESULT_DELETE_TABLE.getStatus())
                .message(RESULT_DELETE_TABLE.getMessage())
                .build();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("test", "1234", authorities(UserRole.USER));
        given(jwtProvider.getAuthentication(anyString())).willReturn(authentication);
        given(tableService.deleteTable(anyLong(), anyString())).willReturn(responseResult);

        //when
        ResultActions result = mockMvc.perform(
                delete("/tables/{tableId}", 1L).header("Authorization","Bearer (accessToken)")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        //then
        result.andExpect(status().isOk())
                .andDo(document("deleteTable",
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer + (????????? ?????? access ??????)")
                        ),
                        pathParameters(
                                parameterWithName("tableId").description("????????? ?????? id")
                        ),
                        responseFields(
                                fieldWithPath("status").type(NUMBER).description("?????? ??????"),
                                fieldWithPath("message").type(STRING).description("?????? ?????????")
                        )
                ));
    }

    @Test
    @DisplayName("????????? ????????? ?????????")
    public void initTable() throws Exception{
        //given
        ResponseResult responseResult = ResponseResult.builder()
                .status(RESULT_INIT_TABLE.getStatus())
                .message(RESULT_INIT_TABLE.getMessage())
                .build();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("test", "1234", authorities(UserRole.USER));
        given(jwtProvider.getAuthentication(anyString())).willReturn(authentication);
        given(tableService.initTable(anyLong(), anyString())).willReturn(responseResult);

        //when
        ResultActions result = mockMvc.perform(
                post("/tables/{tableId}/init", 1L).header("Authorization","Bearer (accessToken)")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        //then
        result.andExpect(status().isOk())
                .andDo(document("initTable",
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer + (????????? ?????? access ??????)")
                        ),
                        pathParameters(
                                parameterWithName("tableId").description("????????? ?????? id")
                        ),
                        responseFields(
                                fieldWithPath("status").type(NUMBER).description("?????? ??????"),
                                fieldWithPath("message").type(STRING).description("?????? ?????????")
                        )
                ));
    }
}
