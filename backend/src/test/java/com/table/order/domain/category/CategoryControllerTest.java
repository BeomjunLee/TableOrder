package com.table.order.domain.category;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.table.order.domain.category.controller.CategoryController;
import com.table.order.domain.category.dto.CategoryDto;
import com.table.order.domain.category.dto.request.RequestAddCategory;
import com.table.order.domain.category.dto.response.ResponseAddCategory;
import com.table.order.domain.category.dto.response.ResponseCategoriesItems;
import com.table.order.domain.category.service.CategoryService;
import com.table.order.domain.item.dto.ItemDto;
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

import java.util.ArrayList;
import java.util.List;

import static com.table.order.global.common.RoleToCollection.authorities;
import static com.table.order.global.common.code.ResultCode.RESULT_ADD_CATEGORY;
import static com.table.order.global.common.code.ResultCode.RESULT_FIND_CATEGORIES_ITEMS;
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
    @DisplayName("카테고리 추가 테스트")
    public void addCategory() throws Exception{
        //given
        CategoryDto categoryDto = CategoryDto.builder()
                .id(1L)
                .name("카테고리 1")
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
                .name("카테고리 1")
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
                                headerWithName("Authorization").description("Bearer + (로그인 요청 access 토큰)")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("카테고리명")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("카테고리 고유 id"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("카테고리명")
                        )
                ));
    }

    @Test
    @DisplayName("카테고리, 메뉴 전체 조회 테스트 (회원)")
    public void findCategoriesUser() throws Exception{
        //given
        List<ItemDto> itemDtos1 = new ArrayList<>();
        List<ItemDto> itemDtos2 = new ArrayList<>();

        for (int i = 1; i <= 2; i++) {
            ItemDto itemDto1 = ItemDto.builder()
                    .id((long) i)
                    .name("메뉴"+i)
                    .description("메뉴 설명")
                    .price(2000*i)
                    .image("이미지 주소")
                    .build();
            itemDtos1.add(itemDto1);

            ItemDto itemDto2 = ItemDto.builder()
                    .id((long) i+2)
                    .name("메뉴"+i)
                    .description("메뉴 설명")
                    .price(2000*i)
                    .image("이미지 주소")
                    .build();
            itemDtos2.add(itemDto2);
        }

        List<CategoryDto> categoryDtos = new ArrayList<>();
        CategoryDto categoryDto1 = CategoryDto.builder()
                .id(5L)
                .name("카테고리1")
                .items(itemDtos1)
                .build();
        CategoryDto categoryDto2 = CategoryDto.builder()
                .id(6L)
                .name("카테고리2")
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
                                headerWithName("Authorization").description("Bearer + (로그인 요청 access 토큰)")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                                fieldWithPath("data.[].id").type(JsonFieldType.NUMBER).description("카테고리 고유 id"),
                                fieldWithPath("data.[].name").type(JsonFieldType.STRING).description("카테고리명"),
                                fieldWithPath("data.[].items.[].id").type(JsonFieldType.NUMBER).description("메뉴 고유 id"),
                                fieldWithPath("data.[].items.[].name").type(JsonFieldType.STRING).description("메뉴명"),
                                fieldWithPath("data.[].items.[].description").type(JsonFieldType.STRING).description("메뉴 설명"),
                                fieldWithPath("data.[].items.[].price").type(JsonFieldType.NUMBER).description("메뉴 가격"),
                                fieldWithPath("data.[].items.[].image").type(JsonFieldType.STRING).description("메뉴 이미지")
                        )
                ));
    }

    @Test
    @DisplayName("카테고리, 메뉴 전체 조회 테스트 (손님)")
    public void findCategoriesCustomer() throws Exception{
        //given
        List<ItemDto> itemDtos1 = new ArrayList<>();
        List<ItemDto> itemDtos2 = new ArrayList<>();

        for (int i = 1; i <= 2; i++) {
            ItemDto itemDto1 = ItemDto.builder()
                    .id((long) i)
                    .name("메뉴"+i)
                    .description("메뉴 설명")
                    .price(2000*i)
                    .image("이미지 주소")
                    .build();
            itemDtos1.add(itemDto1);

            ItemDto itemDto2 = ItemDto.builder()
                    .id((long) i+2)
                    .name("메뉴"+i)
                    .description("메뉴 설명")
                    .price(2000*i)
                    .image("이미지 주소")
                    .build();
            itemDtos2.add(itemDto2);
        }

        List<CategoryDto> categoryDtos = new ArrayList<>();
        CategoryDto categoryDto1 = CategoryDto.builder()
                .id(5L)
                .name("카테고리1")
                .items(itemDtos1)
                .build();
        CategoryDto categoryDto2 = CategoryDto.builder()
                .id(6L)
                .name("카테고리2")
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
                                headerWithName("Authorization").description("Bearer + (로그인 요청 access 토큰)")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                                fieldWithPath("data.[].id").type(JsonFieldType.NUMBER).description("카테고리 고유 id"),
                                fieldWithPath("data.[].name").type(JsonFieldType.STRING).description("카테고리명"),
                                fieldWithPath("data.[].items.[].id").type(JsonFieldType.NUMBER).description("메뉴 고유 id"),
                                fieldWithPath("data.[].items.[].name").type(JsonFieldType.STRING).description("메뉴명"),
                                fieldWithPath("data.[].items.[].description").type(JsonFieldType.STRING).description("메뉴 설명"),
                                fieldWithPath("data.[].items.[].price").type(JsonFieldType.NUMBER).description("메뉴 가격"),
                                fieldWithPath("data.[].items.[].image").type(JsonFieldType.STRING).description("메뉴 이미지")
                        )
                ));
    }
}