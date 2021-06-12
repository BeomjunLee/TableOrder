package com.table.order.domain.category;

import com.table.order.domain.category.dto.CategoryDto;
import com.table.order.domain.category.dto.request.RequestAddCategory;
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
import com.table.order.global.common.exception.CustomIllegalArgumentException;
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

import static com.table.order.global.common.code.CustomErrorCode.ERROR_INVALID_STORE;
import static com.table.order.global.common.code.CustomErrorCode.ERROR_NOT_FOUND_STORE;
import static com.table.order.global.common.code.ResultCode.RESULT_ADD_CATEGORY;
import static com.table.order.global.common.code.ResultCode.RESULT_FIND_CATEGORIES_ITEMS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
                .name("카테고리")
                .build();

        categoryDto = CategoryDto.builder()
                .id(null)
                .name("카테고리")
                .build();

        responseAddCategory = ResponseAddCategory.builder()
                .status(RESULT_ADD_CATEGORY.getStatus())
                .message(RESULT_ADD_CATEGORY.getMessage())
                .data(categoryDto)
                .build();

        store = Store.builder()
                .name("식당")
                .description("식당 설명")
                .licenseImage("이미지 주소")
                .storeStatus(StoreStatus.VALID)
                .build();
        category = Category.createCategory(requestAddCategory, store);
    }

    @Test
    @DisplayName("카테고리 추가 테스트")
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
    @DisplayName("카테고리 추가 실패 테스트 (식당을 찾을 수 없음)")
    public void addCategoryNotFoundStore() throws Exception{
        //given
        given(storeQueryRepository.findByUsernameJoinUser(anyString())).willReturn(Optional.ofNullable(null));

        //when then
        assertThatThrownBy(() -> {
            categoryService.addCategory(requestAddCategory, anyString());
        }).isInstanceOf(CustomIllegalArgumentException.class).hasMessageContaining(ERROR_NOT_FOUND_STORE.getMessage());
    }

    @Test
    @DisplayName("카테고리 추가 실패 테스트 (미승인된 식당)")
    public void addCategoryInvalidStore() throws Exception{
        //given
        store = Store.builder()
                .name("식당")
                .description("식당 설명")
                .licenseImage("이미지 주소")
                .storeStatus(StoreStatus.INVALID)
                .build();

        given(storeQueryRepository.findByUsernameJoinUser(anyString())).willReturn(Optional.ofNullable(store));

        //when then
        assertThatThrownBy(() -> {
            categoryService.addCategory(requestAddCategory, anyString());
        }).isInstanceOf(CustomAccessDeniedException.class).hasMessageContaining(ERROR_INVALID_STORE.getMessage());
    }

    @Test
    @DisplayName("카테고리, 메뉴 전체 조회 (회원)")
    public void findCategoriesUser() throws Exception{
        //given
        List<Category> categories = new ArrayList<>();
        Item item = Item.builder()
                .name("메뉴")
                .description("메뉴 설명")
                .price(1000)
                .image("이미지 주소")
                .store(store)
                .build();

        for (int i = 0; i < 3; i++) {
            category.getItems().add(item);
            categories.add(category);
        }

        List<ItemDto> itemDtos = new ArrayList<>();
        ItemDto itemDto = ItemDto.builder()
                .id(null)
                .name("메뉴")
                .description("메뉴 설명")
                .price(1000)
                .image("이미지 주소")
                .build();
        for (int i = 0; i < 3; i++) {
            itemDtos.add(itemDto);
        }

        List<CategoryDto> categoryDtos = new ArrayList<>();
        CategoryDto categoryDto = CategoryDto.builder()
                .id(null)
                .name("카테고리")
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
    @DisplayName("카테고리, 메뉴 전체 조회 (손님)")
    public void findCategoriesCustomer() throws Exception{
        //given
        List<Category> categories = new ArrayList<>();
        Item item = Item.builder()
                .name("메뉴")
                .description("메뉴 설명")
                .price(1000)
                .image("이미지 주소")
                .store(store)
                .build();

        for (int i = 0; i < 3; i++) {
            category.getItems().add(item);
            categories.add(category);
        }

        List<ItemDto> itemDtos = new ArrayList<>();
        ItemDto itemDto = ItemDto.builder()
                .name("메뉴")
                .description("메뉴 설명")
                .price(1000)
                .image("이미지 주소")
                .build();
        for (int i = 0; i < 3; i++) {
            itemDtos.add(itemDto);
        }

        List<CategoryDto> categoryDtos = new ArrayList<>();
        CategoryDto categoryDto = CategoryDto.builder()
                .id(null)
                .name("카테고리")
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
}