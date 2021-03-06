package com.table.order.domain.category;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.table.order.domain.category.controller.CategoryController;
import com.table.order.domain.category.dto.CategoryDto;
import com.table.order.domain.category.dto.request.RequestAddCategory;
import com.table.order.domain.category.dto.request.RequestUpdateCategory;
import com.table.order.domain.category.dto.response.ResponseAddCategory;
import com.table.order.domain.category.dto.response.ResponseCategoriesItems;
import com.table.order.domain.category.service.CategoryService;
import com.table.order.domain.item.dto.ItemDto;
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

@WebMvcTest(CategoryController.class)
@AutoConfigureRestDocs
class CategoryControllerTest {
    @MockBean
    private CategoryService categoryService;
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
        CategoryDto categoryDto = CategoryDto.builder()
                .id(1L)
                .name("???????????? 1")
                .build();

        ResponseAddCategory responseAddCategory = ResponseAddCategory.builder()
                .status(RESULT_ADD_CATEGORY.getStatus())
                .message(RESULT_ADD_CATEGORY.getMessage())
                .data(categoryDto)
                .build();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("test", "1234", authorities(UserRole.USER));
        given(jwtProvider.getAuthentication(anyString())).willReturn(authentication);
        given(categoryService.addCategory(any(RequestAddCategory.class), anyString())).willReturn(responseAddCategory);

        //when
        RequestAddCategory requestAddCategory = RequestAddCategory.builder()
                .name("???????????? 1")
                .build();

        ResultActions result = mockMvc.perform(
                post("/categories").header("Authorization","Bearer (accessToken)")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestAddCategory))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        //then
        result.andExpect(status().isCreated())
                .andDo(document("addCategory",
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer + (????????? ?????? access ??????)")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("???????????????")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("?????? ?????????"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("???????????? ?????? id"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("???????????????")
                        )
                ));
    }

    @Test
    @DisplayName("????????????, ?????? ?????? ?????? ????????? (??????)")
    public void findCategoriesUser() throws Exception{
        //given
        List<ItemDto> itemDtos1 = new ArrayList<>();
        List<ItemDto> itemDtos2 = new ArrayList<>();

        for (int i = 1; i <= 2; i++) {
            ItemDto itemDto1 = ItemDto.builder()
                    .id((long) i)
                    .name("??????"+i)
                    .description("?????? ??????")
                    .price(2000*i)
                    .image("????????? ??????")
                    .build();
            itemDtos1.add(itemDto1);

            ItemDto itemDto2 = ItemDto.builder()
                    .id((long) i+2)
                    .name("??????"+i)
                    .description("?????? ??????")
                    .price(2000*i)
                    .image("????????? ??????")
                    .build();
            itemDtos2.add(itemDto2);
        }

        List<CategoryDto> categoryDtos = new ArrayList<>();
        CategoryDto categoryDto1 = CategoryDto.builder()
                .id(5L)
                .name("????????????1")
                .items(itemDtos1)
                .build();
        CategoryDto categoryDto2 = CategoryDto.builder()
                .id(6L)
                .name("????????????2")
                .items(itemDtos2)
                .build();
        categoryDtos.add(categoryDto1);
        categoryDtos.add(categoryDto2);

        ResponseCategoriesItems responseCategoriesItems = ResponseCategoriesItems.builder()
                .status(RESULT_FIND_CATEGORIES_ITEMS.getStatus())
                .message(RESULT_FIND_CATEGORIES_ITEMS.getMessage())
                .data(categoryDtos)
                .build();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("test", "1234", authorities(UserRole.USER));
        given(jwtProvider.getAuthentication(anyString())).willReturn(authentication);
        given(categoryService.findCategoriesUser(anyString())).willReturn(responseCategoriesItems);

        //when
        ResultActions result = mockMvc.perform(
                get("/categories/items").header("Authorization","Bearer (accessToken)")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        //then
        result.andExpect(status().isOk())
                .andDo(document("findCategoriesItemsUser",
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer + (????????? ?????? access ??????)")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("?????? ?????????"),
                                fieldWithPath("data.[].id").type(JsonFieldType.NUMBER).description("???????????? ?????? id"),
                                fieldWithPath("data.[].name").type(JsonFieldType.STRING).description("???????????????"),
                                fieldWithPath("data.[].items.[].id").type(JsonFieldType.NUMBER).description("?????? ?????? id"),
                                fieldWithPath("data.[].items.[].name").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("data.[].items.[].description").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("data.[].items.[].price").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("data.[].items.[].image").type(JsonFieldType.STRING).description("?????? ?????????")
                        )
                ));
    }

    @Test
    @DisplayName("????????????, ?????? ?????? ?????? ????????? (??????)")
    public void findCategoriesCustomer() throws Exception{
        //given
        List<ItemDto> itemDtos1 = new ArrayList<>();
        List<ItemDto> itemDtos2 = new ArrayList<>();

        for (int i = 1; i <= 2; i++) {
            ItemDto itemDto1 = ItemDto.builder()
                    .id((long) i)
                    .name("??????"+i)
                    .description("?????? ??????")
                    .price(2000*i)
                    .image("????????? ??????")
                    .build();
            itemDtos1.add(itemDto1);

            ItemDto itemDto2 = ItemDto.builder()
                    .id((long) i+2)
                    .name("??????"+i)
                    .description("?????? ??????")
                    .price(2000*i)
                    .image("????????? ??????")
                    .build();
            itemDtos2.add(itemDto2);
        }

        List<CategoryDto> categoryDtos = new ArrayList<>();
        CategoryDto categoryDto1 = CategoryDto.builder()
                .id(5L)
                .name("????????????1")
                .items(itemDtos1)
                .build();
        CategoryDto categoryDto2 = CategoryDto.builder()
                .id(6L)
                .name("????????????2")
                .items(itemDtos2)
                .build();
        categoryDtos.add(categoryDto1);
        categoryDtos.add(categoryDto2);

        ResponseCategoriesItems responseCategoriesItems = ResponseCategoriesItems.builder()
                .status(RESULT_FIND_CATEGORIES_ITEMS.getStatus())
                .message(RESULT_FIND_CATEGORIES_ITEMS.getMessage())
                .data(categoryDtos)
                .build();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("test", "1234", authorities(UserRole.USER));
        given(jwtProvider.getAuthentication(anyString())).willReturn(authentication);
        given(categoryService.findCategoriesCustomer(anyString())).willReturn(responseCategoriesItems);

        //when
        ResultActions result = mockMvc.perform(
                get("/app/categories/items").header("Authorization","Bearer (accessToken)")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        //then
        result.andExpect(status().isOk())
                .andDo(document("findCategoriesItemsCustomer",
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer + (????????? ?????? access ??????)")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("?????? ?????????"),
                                fieldWithPath("data.[].id").type(JsonFieldType.NUMBER).description("???????????? ?????? id"),
                                fieldWithPath("data.[].name").type(JsonFieldType.STRING).description("???????????????"),
                                fieldWithPath("data.[].items.[].id").type(JsonFieldType.NUMBER).description("?????? ?????? id"),
                                fieldWithPath("data.[].items.[].name").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("data.[].items.[].description").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("data.[].items.[].price").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("data.[].items.[].image").type(JsonFieldType.STRING).description("?????? ?????????")
                        )
                ));
    }

    @Test
    @DisplayName("???????????? ?????? ?????????")
    public void deleteCategory() throws Exception{
        //given
        ResponseResult responseResult = ResponseResult.builder()
                .status(RESULT_DELETE_CATEGORY.getStatus())
                .message(RESULT_DELETE_CATEGORY.getMessage())
                .build();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("test", "1234", authorities(UserRole.USER));
        given(jwtProvider.getAuthentication(anyString())).willReturn(authentication);
        given(categoryService.deleteCategory(anyLong(), anyString())).willReturn(responseResult);
        //when
        ResultActions result = mockMvc.perform(
                delete("/categories/{categoryId}", 1L).header("Authorization","Bearer (accessToken)")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        //then
        result.andExpect(status().isOk())
                .andDo(document("deleteCategory",
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer + (????????? ?????? access ??????)")
                        ),
                        pathParameters(
                                parameterWithName("categoryId").description("???????????? ?????? id")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("?????? ?????????")
                        )
                ));
    }

    @Test
    @DisplayName("???????????? ?????? ?????????")
    public void updateCategory() throws Exception{
        //given
        ResponseResult responseResult = ResponseResult.builder()
                .status(RESULT_UPDATE_CATEGORY.getStatus())
                .message(RESULT_UPDATE_CATEGORY.getMessage())
                .build();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("test", "1234", authorities(UserRole.USER));
        given(jwtProvider.getAuthentication(anyString())).willReturn(authentication);
        given(categoryService.updateCategory(anyLong(), anyString(), any(RequestUpdateCategory.class))).willReturn(responseResult);
        //when
        RequestUpdateCategory requestUpdateCategory = RequestUpdateCategory.builder()
                .name("????????? ???????????????")
                .build();

        ResultActions result = mockMvc.perform(
                put("/categories/{categoryId}", 1L).header("Authorization","Bearer (accessToken)")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUpdateCategory)))
                .andDo(print());

        //then
        result.andExpect(status().isOk())
                .andDo(document("updateCategory",
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer + (????????? ?????? access ??????)")
                        ),
                        pathParameters(
                                parameterWithName("categoryId").description("???????????? ?????? id")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("???????????????")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("?????? ?????????")
                        )
                ));
    }
}