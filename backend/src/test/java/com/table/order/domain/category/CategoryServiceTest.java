package com.table.order.domain.category;

import com.table.order.domain.category.dto.CategoryDto;
import com.table.order.domain.category.dto.request.RequestAddCategory;
import com.table.order.domain.category.dto.request.RequestUpdateCategory;
import com.table.order.domain.category.dto.response.ResponseAddCategory;
import com.table.order.domain.category.dto.response.ResponseCategoriesItems;
import com.table.order.domain.category.entity.Category;
import com.table.order.domain.category.repository.CategoryQueryRepository;
import com.table.order.domain.category.repository.CategoryRepository;
import com.table.order.domain.category.service.CategoryService;
import com.table.order.domain.item.dto.ItemDto;
import com.table.order.domain.item.entity.Item;
import com.table.order.domain.store.entity.Store;
import com.table.order.domain.store.entity.StoreStatus;
import com.table.order.domain.store.exception.CustomAccessDeniedException;
import com.table.order.domain.store.repository.StoreQueryRepository;
import com.table.order.global.common.exception.CustomConflictException;
import com.table.order.global.common.exception.CustomIllegalArgumentException;
import com.table.order.global.common.response.ResponseResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.table.order.global.common.code.CustomErrorCode.*;
import static com.table.order.global.common.code.ResultCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @InjectMocks
    private CategoryService categoryService;
    @Mock
    private StoreQueryRepository storeQueryRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryQueryRepository categoryQueryRepository;

    private RequestAddCategory requestAddCategory;
    private ResponseAddCategory responseAddCategory;
    private Category category;
    private CategoryDto categoryDto;
    private Store store;

    @BeforeEach
    public void init() {
        requestAddCategory = RequestAddCategory.builder()
                .name("????????????")
                .build();

        categoryDto = CategoryDto.builder()
                .id(null)
                .name("????????????")
                .build();

        responseAddCategory = ResponseAddCategory.builder()
                .status(RESULT_ADD_CATEGORY.getStatus())
                .message(RESULT_ADD_CATEGORY.getMessage())
                .data(categoryDto)
                .build();

        store = Store.builder()
                .name("??????")
                .description("?????? ??????")
                .licenseImage("????????? ??????")
                .storeStatus(StoreStatus.VALID)
                .build();
        category = Category.createCategory(requestAddCategory, store);
    }

    @Test
    @DisplayName("???????????? ?????? ?????????")
    public void addCategory() throws Exception{
        //given
        given(storeQueryRepository.findByUsernameJoinUser(anyString())).willReturn(Optional.ofNullable(store));
        given(categoryRepository.save(any(Category.class))).willReturn(category);

        //when
        ResponseAddCategory response = categoryService.addCategory(requestAddCategory, anyString());

        //then
        assertThat(response).usingRecursiveComparison().isEqualTo(responseAddCategory);
    }

    @Test
    @DisplayName("???????????? ?????? ?????? ????????? (????????? ?????? ??? ??????)")
    public void addCategoryNotFoundStore() throws Exception{
        //given
        given(storeQueryRepository.findByUsernameJoinUser(anyString())).willReturn(Optional.ofNullable(null));

        //when then
        assertThatThrownBy(() -> {
            categoryService.addCategory(requestAddCategory, anyString());
        }).isInstanceOf(CustomIllegalArgumentException.class).hasMessageContaining(ERROR_NOT_FOUND_STORE.getMessage());
    }

    @Test
    @DisplayName("???????????? ?????? ?????? ????????? (???????????? ??????)")
    public void addCategoryInvalidStore() throws Exception{
        //given
        store = Store.builder()
                .name("??????")
                .description("?????? ??????")
                .licenseImage("????????? ??????")
                .storeStatus(StoreStatus.INVALID)
                .build();

        given(storeQueryRepository.findByUsernameJoinUser(anyString())).willReturn(Optional.ofNullable(store));

        //when then
        assertThatThrownBy(() -> {
            categoryService.addCategory(requestAddCategory, anyString());
        }).isInstanceOf(CustomConflictException.class).hasMessageContaining(ERROR_INVALID_STORE.getMessage());
    }

    @Test
    @DisplayName("????????????, ?????? ?????? ?????? (??????)")
    public void findCategoriesUser() throws Exception{
        //given
        List<Category> categories = new ArrayList<>();
        Item item = Item.builder()
                .name("??????")
                .description("?????? ??????")
                .price(1000)
                .image("????????? ??????")
                .store(store)
                .build();

        for (int i = 0; i < 3; i++) {
            category.getItems().add(item);
            categories.add(category);
        }

        List<ItemDto> itemDtos = new ArrayList<>();
        ItemDto itemDto = ItemDto.builder()
                .id(null)
                .name("??????")
                .description("?????? ??????")
                .price(1000)
                .image("????????? ??????")
                .build();
        for (int i = 0; i < 3; i++) {
            itemDtos.add(itemDto);
        }

        List<CategoryDto> categoryDtos = new ArrayList<>();
        CategoryDto categoryDto = CategoryDto.builder()
                .id(null)
                .name("????????????")
                .items(itemDtos)
                .build();
        for (int i = 0; i < 3; i++) {
            categoryDtos.add(categoryDto);
        }

        ResponseCategoriesItems responseCategoriesItems = ResponseCategoriesItems.builder()
                .status(RESULT_FIND_CATEGORIES_ITEMS.getStatus())
                .message(RESULT_FIND_CATEGORIES_ITEMS.getMessage())
                .data(categoryDtos)
                .build();
        List<ResponseCategoriesItems> responseCategories = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            responseCategories.add(responseCategoriesItems);
        }

        given(categoryQueryRepository.findAllJoinUserStoreItem(anyString())).willReturn(categories);

        //when
        ResponseCategoriesItems response = categoryService.findCategoriesUser(anyString());

        //then
        assertThat(response).usingRecursiveComparison().isEqualTo(responseCategoriesItems);
    }

    @Test
    @DisplayName("????????????, ?????? ?????? ?????? (??????)")
    public void findCategoriesCustomer() throws Exception{
        //given
        List<Category> categories = new ArrayList<>();
        Item item = Item.builder()
                .name("??????")
                .description("?????? ??????")
                .price(1000)
                .image("????????? ??????")
                .store(store)
                .build();

        for (int i = 0; i < 3; i++) {
            category.getItems().add(item);
            categories.add(category);
        }

        List<ItemDto> itemDtos = new ArrayList<>();
        ItemDto itemDto = ItemDto.builder()
                .name("??????")
                .description("?????? ??????")
                .price(1000)
                .image("????????? ??????")
                .build();
        for (int i = 0; i < 3; i++) {
            itemDtos.add(itemDto);
        }

        List<CategoryDto> categoryDtos = new ArrayList<>();
        CategoryDto categoryDto = CategoryDto.builder()
                .id(null)
                .name("????????????")
                .items(itemDtos)
                .build();
        for (int i = 0; i < 3; i++) {
            categoryDtos.add(categoryDto);
        }

        ResponseCategoriesItems responseCategoriesItems = ResponseCategoriesItems.builder()
                .status(RESULT_FIND_CATEGORIES_ITEMS.getStatus())
                .message(RESULT_FIND_CATEGORIES_ITEMS.getMessage())
                .data(categoryDtos)
                .build();
        List<ResponseCategoriesItems> responseCategories = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            responseCategories.add(responseCategoriesItems);
        }
        given(categoryQueryRepository.findAllJoinCustomerStoreItem(anyString())).willReturn(categories);

        //when
        ResponseCategoriesItems response = categoryService.findCategoriesCustomer(anyString());

        //then
        assertThat(response).usingRecursiveComparison().isEqualTo(responseCategoriesItems);
    }

    @Test
    @DisplayName("???????????? ?????? ?????????")
    public void deleteCategory() throws Exception{
        //given
        ResponseResult responseResult = ResponseResult.builder()
                .status(RESULT_DELETE_CATEGORY.getStatus())
                .message(RESULT_DELETE_CATEGORY.getMessage())
                .build();

        given(categoryQueryRepository.findByIdJoinStoreUser(anyLong(), anyString())).willReturn(Optional.ofNullable(category));

        //when
        ResponseResult response = categoryService.deleteCategory(anyLong(), anyString());

        //then
        assertThat(response).usingRecursiveComparison().isEqualTo(responseResult);
    }

    @Test
    @DisplayName("???????????? ?????? ?????? ?????????")
    public void deleteCategoryFail() throws Exception{
        //given
        given(categoryQueryRepository.findByIdJoinStoreUser(anyLong(), anyString())).willReturn(Optional.ofNullable(null));

        //when then
        assertThatThrownBy(() -> {
            categoryService.deleteCategory(anyLong(), anyString());
        }).isInstanceOf(CustomIllegalArgumentException.class).hasMessageContaining(ERROR_DELETE_CATEGORY.getMessage());
    }

    @Test
    @DisplayName("???????????? ?????? ?????????")
    public void updateCategory() throws Exception{
        //given
        ResponseResult responseResult = ResponseResult.builder()
                .status(RESULT_UPDATE_CATEGORY.getStatus())
                .message(RESULT_UPDATE_CATEGORY.getMessage())
                .build();

        RequestUpdateCategory requestUpdateCategory = RequestUpdateCategory.builder()
                .name("????????? ????????????")
                .build();

        given(categoryQueryRepository.findByIdJoinStoreUser(anyLong(), anyString())).willReturn(Optional.ofNullable(category));

        //when
        ResponseResult response = categoryService.updateCategory(anyLong(), anyString(), requestUpdateCategory);

        //then
        assertThat(response).usingRecursiveComparison().isEqualTo(responseResult);
    }

    @Test
    @DisplayName("???????????? ?????? ?????? ?????????")
    public void updateCategoryFail() throws Exception{
        //given
        RequestUpdateCategory requestUpdateCategory = RequestUpdateCategory.builder()
                .name("????????? ????????????")
                .build();

        given(categoryQueryRepository.findByIdJoinStoreUser(anyLong(), anyString())).willReturn(Optional.ofNullable(null));

        //when then
        assertThatThrownBy(() -> {
            categoryService.updateCategory(anyLong(), anyString(), requestUpdateCategory);
        }).isInstanceOf(CustomIllegalArgumentException.class).hasMessageContaining(ERROR_UPDATE_CATEGORY.getMessage());
    }

}