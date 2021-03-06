package com.table.order.domain.item;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.table.order.domain.category.dto.request.RequestUpdateCategory;
import com.table.order.domain.item.controller.ItemController;
import com.table.order.domain.item.dto.ItemDto;
import com.table.order.domain.item.dto.request.RequestAddItem;
import com.table.order.domain.item.dto.request.RequestUpdateItem;
import com.table.order.domain.item.dto.response.ResponseAddItem;
import com.table.order.domain.item.service.ItemService;
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
import static com.table.order.global.common.RoleToCollection.authorities;
import static com.table.order.global.common.code.ResultCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureRestDocs
class ItemControllerTest {
    @MockBean
    private ItemService itemService;
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
    @DisplayName("???????????? ?????? ?????????")
    public void addCategory() throws Exception{
        //given
        ItemDto itemDto = ItemDto.builder()
                .id(2L)
                .name("?????? 1")
                .description("?????? ??????")
                .price(1000)
                .image("?????????")
                .build();

        ResponseAddItem responseAddItem = ResponseAddItem.builder()
                .status(RESULT_ADD_ITEM.getStatus())
                .message(RESULT_ADD_ITEM.getMessage())
                .data(itemDto)
                .build();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("test", "1234", authorities(UserRole.USER));
        given(jwtProvider.getAuthentication(anyString())).willReturn(authentication);
        given(itemService.addItem(any(RequestAddItem.class), anyString())).willReturn(responseAddItem);

        //when
        RequestAddItem requestAddItem = RequestAddItem.builder()
                .name("?????? 1")
                .description("?????? ??????")
                .price(1000)
                .image("?????????")
                .categoryId(1L)
                .build();

        ResultActions result = mockMvc.perform(
                post("/items").header("Authorization","Bearer (accessToken)")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestAddItem))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        //then
        result.andExpect(status().isCreated())
                .andDo(document("addItem",
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer + (????????? ?????? access ??????)")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("??????"),
                                fieldWithPath("image").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("categoryId").type(JsonFieldType.NUMBER).description("???????????? ?????? id")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("?????? ?????????"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("?????? ?????? id"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("data.description").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("data.price").type(JsonFieldType.NUMBER).description("??????"),
                                fieldWithPath("data.image").type(JsonFieldType.STRING).description("????????? ??????")
                        )
                ));
    }

    @Test
    @DisplayName("?????? ?????? ?????????")
    public void deleteItem() throws Exception{
        //given
        ResponseResult responseResult = ResponseResult.builder()
                .status(RESULT_DELETE_ITEM.getStatus())
                .message(RESULT_DELETE_ITEM.getMessage())
                .build();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("test", "1234", authorities(UserRole.USER));
        given(jwtProvider.getAuthentication(anyString())).willReturn(authentication);
        given(itemService.deleteItem(anyLong(), anyString())).willReturn(responseResult);

        //when
        ResultActions result = mockMvc.perform(
                delete("/items/{itemId}", 1L).header("Authorization","Bearer (accessToken)")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        //then
        result.andExpect(status().isOk())
                .andDo(document("deleteItem",
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer + (????????? ?????? access ??????)")
                        ),
                        pathParameters(
                                parameterWithName("itemId").description("?????? ?????? id")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("?????? ?????????")
                        )
                ));
    }

    @Test
    @DisplayName("?????? ?????? ?????????")
    public void updateItem() throws Exception{
        //given
        ResponseResult responseResult = ResponseResult.builder()
                .status(RESULT_UPDATE_ITEM.getStatus())
                .message(RESULT_UPDATE_ITEM.getMessage())
                .build();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("test", "1234", authorities(UserRole.USER));
        given(jwtProvider.getAuthentication(anyString())).willReturn(authentication);
        given(itemService.updateItem(anyLong(), anyString(), any(RequestUpdateItem.class))).willReturn(responseResult);
        //when
        RequestUpdateItem requestUpdateItem = RequestUpdateItem.builder()
                .name("????????? ?????????")
                .description("????????? ??????")
                .price(100000)
                .image("????????? ????????? ??????")
                .build();

        ResultActions result = mockMvc.perform(
                put("/items/{itemId}", 1L).header("Authorization","Bearer (accessToken)")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUpdateItem)))
                .andDo(print());

        //then
        result.andExpect(status().isOk())
                .andDo(document("updateItem",
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer + (????????? ?????? access ??????)")
                        ),
                        pathParameters(
                                parameterWithName("itemId").description("?????? ?????? id")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("image").type(JsonFieldType.STRING).description("?????? ????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("?????? ?????????")
                        )
                ));
    }
}