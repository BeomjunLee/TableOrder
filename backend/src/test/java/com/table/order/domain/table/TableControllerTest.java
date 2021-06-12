package com.table.order.domain.table;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.table.order.domain.order.dto.OrderDto;
import com.table.order.domain.order.entity.OrderStatus;
import com.table.order.domain.table.controller.TableController;
import com.table.order.domain.table.dto.TableDto;
import com.table.order.domain.table.dto.request.RequestAddTable;
import com.table.order.domain.table.dto.response.ResponseAddTable;
import com.table.order.domain.table.dto.response.ResponseTables;
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
import static com.table.order.global.common.code.ResultCode.RESULT_ADD_TABLE;
import static com.table.order.global.common.code.ResultCode.RESULT_FIND_TABLES;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
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
                                fieldWithPath("data.totalPrice").type(JsonFieldType.NUMBER).description("테이블 총 주문 가격"),
                                fieldWithPath("data.tableStatus").type(JsonFieldType.STRING).description("테이블 상태")
                        )
                ));
    }
    
    @Test
    @DisplayName("테이블 전체 조회 테스트")
    public void findTables() throws Exception{
        //given
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("test", "1234", authorities(UserRole.USER));
        given(jwtProvider.getAuthentication(anyString())).willReturn(authentication);

        List<OrderDto> orders = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            OrderDto orderDto = OrderDto.builder()
                    .id((long) i)
                    .name("음식"+i)
                    .orderPrice(i * 5000)
                    .count(i)
                    .request("잘부탁드립니다")
                    .orderStatus(OrderStatus.ORDER)
                    .build();
            orders.add(orderDto);
        }

        List<TableDto> tables = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            TableDto tableDto = TableDto.builder()
                    .id(1L)
                    .name("테이블 1")
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
                                headerWithName("Authorization").description("Bearer + (로그인 요청 access 토큰)")
                        ),
                        requestParameters(
                                parameterWithName("page").description("현재 페이지"),
                                parameterWithName("size").description("한 페이지당 조회할 데이터 개수(생략하면 default 값)")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                                fieldWithPath("data.content.[].id").type(JsonFieldType.NUMBER).description("테이블 고유 id"),
                                fieldWithPath("data.content.[].name").type(JsonFieldType.STRING).description("테이블명"),
                                fieldWithPath("data.content.[].numberOfPeople").type(JsonFieldType.NUMBER).description("테이블 좌석 수"),
                                fieldWithPath("data.content.[].totalPrice").type(JsonFieldType.NUMBER).description("테이블 총 주문 가격"),
                                fieldWithPath("data.content.[].tableStatus").type(JsonFieldType.STRING).description("테이블 상태"),

                                fieldWithPath("data.content.[].orders.[].id").type(JsonFieldType.NUMBER).description("주문 고유 id"),
                                fieldWithPath("data.content.[].orders.[].name").type(JsonFieldType.STRING).description("메뉴 이름"),
                                fieldWithPath("data.content.[].orders.[].orderPrice").type(JsonFieldType.NUMBER).description("주문 가격"),
                                fieldWithPath("data.content.[].orders.[].count").type(JsonFieldType.NUMBER).description("주문 갯수"),
                                fieldWithPath("data.content.[].orders.[].request").type(JsonFieldType.STRING).description("주문 요청사항"),
                                fieldWithPath("data.content.[].orders.[].orderStatus").type(JsonFieldType.STRING).description("주문 상태"),

                                fieldWithPath("data.pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 됐는지 여부"),
                                fieldWithPath("data.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬 안됐는지 여부"),
                                fieldWithPath("data.pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("데이터가 비었는지 여부"),

                                fieldWithPath("data.pageable.pageNumber").type(JsonFieldType.NUMBER).description("현제 페이지 번호"),
                                fieldWithPath("data.pageable.pageSize").type(JsonFieldType.NUMBER).description("한 페이지당 조회할 데이터 개수"),
                                fieldWithPath("data.pageable.offset").type(JsonFieldType.NUMBER).description("몇번째 데이터인지 (0부터 시작)"),
                                fieldWithPath("data.pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 정보를 포함하는지 여부"),
                                fieldWithPath("data.pageable.unpaged").type(JsonFieldType.BOOLEAN).description("페이징 정보를 안포함하는지 여부"),

                                fieldWithPath("data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지인지 여부"),
                                fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 개수"),
                                fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("테이블 총 데이터 개수"),
                                fieldWithPath("data.first").type(JsonFieldType.BOOLEAN).description("첫번째 페이지인지 여부"),
                                fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER).description("요청 페이지에서 조회 된 데이터 개수"),
                                fieldWithPath("data.number").type(JsonFieldType.NUMBER).description("현제 페이지 번호"),
                                fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("한 페이지당 조회할 데이터 개수"),

                                fieldWithPath("data.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 됐는지 여부"),
                                fieldWithPath("data.sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬 안됐는지 여부"),
                                fieldWithPath("data.sort.empty").type(JsonFieldType.BOOLEAN).description("데이터가 비었는지 여부"),

                                fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN).description("데이터가 비었는지 여부")
                        )
                ));
    }
}
