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
    @DisplayName("카테고리 추가 테스트")
    public void addCategory() throws Exception{
        //given
        ItemDto itemDto = ItemDto.builder()
                .id(2L)
                .name("메뉴 1")
                .description("메뉴 설명")
                .price(1000)
                .image("이미지")
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
                .name("메뉴 1")
                .description("메뉴 설명")
                .price(1000)
                .image("이미지")
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
                                headerWithName("Authorization").description("Bearer + (로그인 요청 access 토큰)")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("메뉴명"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("설명"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("가격"),
                                fieldWithPath("image").type(JsonFieldType.STRING).description("이미지 주소"),
                                fieldWithPath("categoryId").type(JsonFieldType.NUMBER).description("카테고리 고유 id")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("메뉴 고유 id"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("메뉴명"),
                                fieldWithPath("data.description").type(JsonFieldType.STRING).description("설명"),
                                fieldWithPath("data.price").type(JsonFieldType.NUMBER).description("가격"),
                                fieldWithPath("data.image").type(JsonFieldType.STRING).description("이미지 주소")
                        )
                ));
    }

    @Test
    @DisplayName("메뉴 삭제 테스트")
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
                                headerWithName("Authorization").description("Bearer + (로그인 요청 access 토큰)")
                        ),
                        pathParameters(
                                parameterWithName("itemId").description("메뉴 고유 id")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지")
                        )
                ));
    }

    @Test
    @DisplayName("메뉴 수정 테스트")
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
                .name("수정된 메뉴명")
                .description("수정된 설명")
                .price(100000)
                .image("수정된 이미지 주소")
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
                                headerWithName("Authorization").description("Bearer + (로그인 요청 access 토큰)")
                        ),
                        pathParameters(
                                parameterWithName("itemId").description("메뉴 고유 id")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("메뉴명"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("메뉴 설명"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("메뉴 가격"),
                                fieldWithPath("image").type(JsonFieldType.STRING).description("메뉴 이미지 주소")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지")
                        )
                ));
    }
}