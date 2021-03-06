package com.table.order.domain.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.table.order.domain.item.dto.OrderItemDto;
import com.table.order.domain.order.controller.OrderController;
import com.table.order.domain.order.dto.OrderDto;
import com.table.order.domain.order.dto.request.OrderIdDto;
import com.table.order.domain.order.dto.request.RequestChangeOrderStatus;
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
    @DisplayName("?????? ?????? ?????????")
    public void createStore() throws Exception{
        //given
        List<OrderDto> orderDtos = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            OrderDto orderDto = OrderDto.builder()
                    .id((long) i)
                    .name("??????"+i)
                    .orderPrice(1000 * i)
                    .count(i)
                    .orderStatus(OrderStatus.ORDER)
                    .request("?????????????????????")
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
                .request("?????????????????????")
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
                                headerWithName("Authorization").description("Bearer + (????????? ?????? access ??????)")
                        ),
                        requestFields(
                                fieldWithPath("items.[].id").type(JsonFieldType.NUMBER).description("?????? ?????? id"),
                                fieldWithPath("items.[].count").type(JsonFieldType.NUMBER).description("?????? ?????? ??????"),
                                fieldWithPath("request").type(JsonFieldType.STRING).description("?????? ????????????")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("?????? ?????????"),
                                fieldWithPath("data.[].id").type(JsonFieldType.NUMBER).description("?????? ?????? id"),
                                fieldWithPath("data.[].name").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("data.[].orderPrice").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("data.[].count").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("data.[].request").type(JsonFieldType.STRING).description("?????? ????????????"),
                                fieldWithPath("data.[].orderStatus").type(JsonFieldType.STRING).description("?????? ??????")
                        )
                ));
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????????")
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
                                headerWithName("Authorization").description("Bearer + (????????? ?????? access ??????)")
                        ),
                        pathParameters(
                                parameterWithName("orderId").description("?????? ?????? id")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("?????? ?????????")
                        )
                ));
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????????")
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
                                headerWithName("Authorization").description("Bearer + (????????? ?????? access ??????)")
                        ),
                        pathParameters(
                                parameterWithName("orderId").description("?????? ?????? id")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("?????? ?????????")
                                )
                ));
    }

    @Test
    @DisplayName("?????? ?????? ?????? ????????? (?????????)")
    public void changeOrderStatusCooked() throws Exception{
        //given
        List<OrderIdDto> orderDtos = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            OrderIdDto orderDto = OrderIdDto.builder()
                    .id((long) i)
                    .build();
            orderDtos.add(orderDto);
        }

        ResponseResult responseResult = ResponseResult.builder()
                .status(RESULT_COOK_ORDER.getStatus())
                .message(RESULT_COOK_ORDER.getMessage())
                .build();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("test", "1234", authorities(UserRole.USER));
        given(jwtProvider.getAuthentication(anyString())).willReturn(authentication);
        given(orderService.changeOrderStatusCooked(any(), anyString())).willReturn(responseResult);

        RequestChangeOrderStatus requestChangeOrderStatus = RequestChangeOrderStatus.builder()
                .ids(orderDtos)
                .build();

        //when
        ResultActions result = mockMvc.perform(
                post("/orders/cook", 1L).header("Authorization","Bearer (accessToken)")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestChangeOrderStatus))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        //then
        result.andExpect(status().isOk())
                .andDo(document("changeOrderStatusCooked",
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer + (????????? ?????? access ??????)")
                        ),
                        requestFields(
                                fieldWithPath("ids.[].id").type(JsonFieldType.NUMBER).description("?????? ?????? id")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("?????? ?????????")
                        )
                ));
    }

    @Test
    @DisplayName("?????? ?????? ?????? ????????? (?????? ??????)")
    public void changeOrderStatusCookComp() throws Exception{
        //given
        List<OrderIdDto> orderDtos = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            OrderIdDto orderDto = OrderIdDto.builder()
                    .id((long) i)
                    .build();
            orderDtos.add(orderDto);
        }

        ResponseResult responseResult = ResponseResult.builder()
                .status(RESULT_COOK_COMP_ORDER.getStatus())
                .message(RESULT_COOK_COMP_ORDER.getMessage())
                .build();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("test", "1234", authorities(UserRole.USER));
        given(jwtProvider.getAuthentication(anyString())).willReturn(authentication);
        given(orderService.changeOrderStatusCookComp(any(), anyString())).willReturn(responseResult);

        RequestChangeOrderStatus requestChangeOrderStatus = RequestChangeOrderStatus.builder()
                .ids(orderDtos)
                .build();
        //when
        ResultActions result = mockMvc.perform(
                post("/orders/cook_comp", 1L).header("Authorization","Bearer (accessToken)")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestChangeOrderStatus))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        //then
        result.andExpect(status().isOk())
                .andDo(document("changeOrderStatusCookComp",
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer + (????????? ?????? access ??????)")
                        ),
                        requestFields(
                                fieldWithPath("ids.[].id").type(JsonFieldType.NUMBER).description("?????? ?????? id")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("?????? ?????????")
                        )
                ));
    }

    @Test
    @DisplayName("?????? ?????? ?????? ????????? (?????? ??????)")
    public void changeOrderStatusComp() throws Exception{
        //given
        List<OrderIdDto> orderDtos = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            OrderIdDto orderDto = OrderIdDto.builder()
                    .id((long) i)
                    .build();
            orderDtos.add(orderDto);
        }

        ResponseResult responseResult = ResponseResult.builder()
                .status(RESULT_COMP_ORDER.getStatus())
                .message(RESULT_COMP_ORDER.getMessage())
                .build();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("test", "1234", authorities(UserRole.USER));
        given(jwtProvider.getAuthentication(anyString())).willReturn(authentication);
        given(orderService.changeOrderStatusComp(any(), anyString())).willReturn(responseResult);

        RequestChangeOrderStatus requestChangeOrderStatus = RequestChangeOrderStatus.builder()
                .ids(orderDtos)
                .build();
        //when
        ResultActions result = mockMvc.perform(
                post("/orders/comp", 1L).header("Authorization","Bearer (accessToken)")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestChangeOrderStatus))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        //then
        result.andExpect(status().isOk())
                .andDo(document("changeOrderStatusComp",
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer + (????????? ?????? access ??????)")
                        ),
                        requestFields(
                                fieldWithPath("ids.[].id").type(JsonFieldType.NUMBER).description("?????? ?????? id")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("?????? ?????????")
                        )
                ));
    }
}