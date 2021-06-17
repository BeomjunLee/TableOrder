package com.table.order.domain.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.table.order.domain.item.dto.OrderItemDto;
import com.table.order.domain.order.controller.OrderController;
import com.table.order.domain.order.dto.OrderDto;
import com.table.order.domain.order.dto.request.RequestCreateOrder;
import com.table.order.domain.order.dto.response.ResponseCreateOrder;
import com.table.order.domain.order.entity.OrderStatus;
import com.table.order.domain.order.service.OrderService;
import com.table.order.domain.store.dto.StoreDto;
import com.table.order.domain.store.dto.request.RequestEnrollStore;
import com.table.order.domain.store.dto.response.ResponseEnrollStore;
import com.table.order.domain.store.service.StoreService;
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
import static com.table.order.global.common.code.ResultCode.RESULT_CANCEL_ORDER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@AutoConfigureRestDocs
class OrderControllerTest {
    @MockBean
    private OrderService orderService;
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
    @DisplayName("주문 생성 테스트")
    public void createStore() throws Exception{
        //given
        List<OrderDto> orderDtos = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            OrderDto orderDto = OrderDto.builder()
                    .id((long) i)
                    .name("메뉴"+i)
                    .orderPrice(1000 * i)
                    .count(i)
                    .orderStatus(OrderStatus.ORDER)
                    .request("잘부탁드립니다")
                    .build();
            orderDtos.add(orderDto);
        }
        ResponseCreateOrder responseCreateOrder = ResponseCreateOrder.builder()
                .status(RESULT_CREATE_ORDER.getStatus())
                .message(RESULT_CREATE_ORDER.getMessage())
                .data(orderDtos)
                .build();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("test", "1234", authorities(UserRole.USER));
        given(jwtProvider.getAuthentication(anyString())).willReturn(authentication);
        given(orderService.createOrder(any(RequestCreateOrder.class), anyString())).willReturn(responseCreateOrder);

        //when
        List<OrderItemDto> orderItemDtos = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            OrderItemDto orderItemDto = OrderItemDto.builder()
                    .id((long) i)
                    .count(i)
                    .build();
            orderItemDtos.add(orderItemDto);
        }

        RequestCreateOrder requestCreateOrder = RequestCreateOrder.builder()
                .items(orderItemDtos)
                .request("잘부탁드립니다")
                .build();

        ResultActions result = mockMvc.perform(
                post("/app/orders").header("Authorization","Bearer (accessToken)")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCreateOrder))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        //then
        result.andExpect(status().isCreated())
                .andDo(document("createOrder",
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer + (로그인 요청 access 토큰)")
                        ),
                        requestFields(
                                fieldWithPath("items.[].id").type(JsonFieldType.NUMBER).description("메뉴 고유 id"),
                                fieldWithPath("items.[].count").type(JsonFieldType.NUMBER).description("메뉴 주문 개수"),
                                fieldWithPath("request").type(JsonFieldType.STRING).description("주문 요청사항")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                                fieldWithPath("data.[].id").type(JsonFieldType.NUMBER).description("주문 고유 id"),
                                fieldWithPath("data.[].name").type(JsonFieldType.STRING).description("메뉴 이름"),
                                fieldWithPath("data.[].orderPrice").type(JsonFieldType.NUMBER).description("주문 가격"),
                                fieldWithPath("data.[].count").type(JsonFieldType.NUMBER).description("주문 갯수"),
                                fieldWithPath("data.[].request").type(JsonFieldType.STRING).description("주문 요청사항"),
                                fieldWithPath("data.[].orderStatus").type(JsonFieldType.STRING).description("주문 상태")
                        )
                ));
    }

    @Test
    @DisplayName("회원 주문 취소 테스트")
    public void cancelOrderUser() throws Exception{
        //given
        ResponseResult responseResult = ResponseResult.builder()
                .status(RESULT_CANCEL_ORDER.getStatus())
                .message(RESULT_CANCEL_ORDER.getMessage())
                .build();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("test", "1234", authorities(UserRole.USER));
        given(jwtProvider.getAuthentication(anyString())).willReturn(authentication);
        given(orderService.cancelOrderUser(anyLong(), anyString())).willReturn(responseResult);

        //when
        ResultActions result = mockMvc.perform(
                post("/orders/{orderId}/cancel", 1L).header("Authorization","Bearer (accessToken)")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        //then
        result.andExpect(status().isOk())
                .andDo(document("cancelOrderUser",
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer + (로그인 요청 access 토큰)")
                        ),
                        pathParameters(
                                parameterWithName("orderId").description("주문 고유 id")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지")
                        )
                ));
    }

    @Test
    @DisplayName("손님 주문 취소 테스트")
    public void cancelOrderCustomer() throws Exception{
        //given
        ResponseResult responseResult = ResponseResult.builder()
                .status(RESULT_CANCEL_ORDER.getStatus())
                .message(RESULT_CANCEL_ORDER.getMessage())
                .build();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("test", "1234", authorities(UserRole.USER));
        given(jwtProvider.getAuthentication(anyString())).willReturn(authentication);
        given(orderService.cancelOrderCustomer(anyLong(), anyString())).willReturn(responseResult);

        //when
        ResultActions result = mockMvc.perform(
                post("/app/orders/{orderId}/cancel", 1L).header("Authorization","Bearer (accessToken)")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        //then
        result.andExpect(status().isOk())
                .andDo(document("cancelOrderCustomer",
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer + (로그인 요청 access 토큰)")
                        ),
                        pathParameters(
                                parameterWithName("orderId").description("주문 고유 id")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지")
                                )
                ));
    }
}