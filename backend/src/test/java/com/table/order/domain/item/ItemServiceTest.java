package com.table.order.domain.item;
import com.table.order.domain.category.dto.request.RequestUpdateCategory;
import com.table.order.domain.category.entity.Category;
import com.table.order.domain.item.dto.ItemDto;
import com.table.order.domain.item.dto.request.RequestAddItem;
import com.table.order.domain.item.dto.request.RequestUpdateItem;
import com.table.order.domain.item.dto.response.ResponseAddItem;
import com.table.order.domain.item.entity.Item;
import com.table.order.domain.item.repository.ItemQueryRepository;
import com.table.order.domain.item.repository.ItemRepository;
import com.table.order.domain.item.service.ItemService;
import com.table.order.domain.store.entity.Store;
import com.table.order.domain.store.entity.StoreStatus;
import com.table.order.domain.store.exception.CustomAccessDeniedException;
import com.table.order.domain.store.repository.StoreQueryRepository;
import com.table.order.global.common.exception.CustomConflictException;
import com.table.order.global.common.exception.CustomIllegalArgumentException;
import com.table.order.global.common.response.ResponseResult;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static com.table.order.global.common.code.CustomErrorCode.*;
import static com.table.order.global.common.code.ResultCode.*;
import static com.table.order.global.common.code.ResultCode.RESULT_UPDATE_CATEGORY;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @InjectMocks
    private ItemService itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private StoreQueryRepository storeQueryRepository;
    @Mock
    private ItemQueryRepository itemQueryRepository;

    private RequestAddItem requestAddItem;
    private ResponseAddItem responseAddItem;
    private ItemDto itemDto;
    private Item item;
    private Store store;
    private Category category;

    @BeforeEach
    public void init() {
        requestAddItem = RequestAddItem.builder()
                .name("?????? 1")
                .description("?????? ??????")
                .price(1000)
                .image("?????????")
                .categoryId(200L)
                .build();

        itemDto = ItemDto.builder()
                .id(null)
                .name("?????? 1")
                .description("?????? ??????")
                .price(1000)
                .image("?????????")
                .build();

        responseAddItem = ResponseAddItem.builder()
                .status(RESULT_ADD_ITEM.getStatus())
                .message(RESULT_ADD_ITEM.getMessage())
                .data(itemDto)
                .build();

        store = Store.builder()
                .name("??????")
                .description("?????? ??????")
                .licenseImage("????????? ??????")
                .storeStatus(StoreStatus.VALID)
                .build();

        category = Category.builder()
                .id(200L)
                .name("???????????? 1")
                .build();
        store.getCategories().add(category);

        item = Item.builder()
                .name(requestAddItem.getName())
                .description(requestAddItem.getDescription())
                .price(requestAddItem.getPrice())
                .image(requestAddItem.getImage())
                .store(store)
                .build();
    }

    @Test
    @DisplayName("?????? ?????? ?????????")
    public void addItem() throws Exception{
        //given
        given(storeQueryRepository.findByUsernameJoinUserCategory(anyString())).willReturn(Optional.ofNullable(store));
        given(itemRepository.save(any(Item.class))).willReturn(item);

        //when
        ResponseAddItem response = itemService.addItem(requestAddItem, anyString());

        //then
        assertThat(response).usingRecursiveComparison().isEqualTo(responseAddItem);
    }

    @Test
    @DisplayName("?????? ?????? ?????? ????????? (???????????? ??????")
    public void addItemInvalidStore() throws Exception{
        //given
        store = Store.builder()
                .name("??????")
                .description("?????? ??????")
                .licenseImage("????????? ??????")
                .storeStatus(StoreStatus.INVALID)
                .build();

        given(storeQueryRepository.findByUsernameJoinUserCategory(anyString())).willReturn(Optional.ofNullable(store));

        //when then
        assertThatThrownBy(() -> {
            itemService.addItem(requestAddItem, anyString());
        }).isInstanceOf(CustomConflictException.class).hasMessageContaining(ERROR_INVALID_STORE.getMessage());
    }

    @Test
    @DisplayName("?????? ?????? ?????? ????????? (????????? ?????? ??? ??????")
    public void addItemNotFoundStore() throws Exception{
        //given
        given(storeQueryRepository.findByUsernameJoinUserCategory(anyString())).willReturn(Optional.ofNullable(null));

        //when then
        assertThatThrownBy(() -> {
            itemService.addItem(requestAddItem, anyString());
        }).isInstanceOf(CustomIllegalArgumentException.class).hasMessageContaining(ERROR_NOT_FOUND_STORE.getMessage());
    }

    @Test
    @DisplayName("?????? ?????? ?????????")
    public void deleteItem() throws Exception{
        //given
        ResponseResult responseResult = ResponseResult.builder()
                .status(RESULT_DELETE_ITEM.getStatus())
                .message(RESULT_DELETE_ITEM.getMessage())
                .build();

        given(itemQueryRepository.findByIdJoinStoreUser(anyLong(), anyString())).willReturn(Optional.of(item));

        //when
        ResponseResult response = itemService.deleteItem(200L, "test");

        //then
        assertThat(response).extracting("status", "message")
                .containsExactly(responseResult.getStatus(), responseResult.getMessage());
    }

    @Test
    @DisplayName("?????? ?????? ?????? ????????? (????????? ?????? ??? ??????")
    public void deleteItemNotFoundStore() throws Exception{
        //given
        given(itemQueryRepository.findByIdJoinStoreUser(anyLong(), anyString())).willReturn(Optional.ofNullable(null));

        //when then
        assertThatThrownBy(() -> {
            itemService.deleteItem(200L, "test");
        }).isInstanceOf(CustomIllegalArgumentException.class).hasMessageContaining(ERROR_NOT_FOUND_ITEM.getMessage());
    }

    @Test
    @DisplayName("?????? ?????? ?????????")
    public void updateItem() throws Exception{
        //given
        ResponseResult responseResult = ResponseResult.builder()
                .status(RESULT_UPDATE_ITEM.getStatus())
                .message(RESULT_UPDATE_ITEM.getMessage())
                .build();

        RequestUpdateItem requestUpdateItem = RequestUpdateItem.builder()
                .name("????????? ?????????")
                .description("????????? ??????")
                .price(100000)
                .image("????????? ????????? ??????")
                .build();

        given(itemQueryRepository.findByIdJoinStoreUser(anyLong(), anyString())).willReturn(Optional.ofNullable(item));

        //when
        ResponseResult response = itemService.updateItem(anyLong(), anyString(), requestUpdateItem);

        //then
        assertThat(response).usingRecursiveComparison().isEqualTo(responseResult);
    }

    @Test
    @DisplayName("?????? ?????? ?????? ????????? (????????? ?????? ??? ??????")
    public void updateItemNotFoundItem() throws Exception{
        //given
        given(itemQueryRepository.findByIdJoinStoreUser(anyLong(), anyString())).willReturn(Optional.ofNullable(null));

        //when then
        assertThatThrownBy(() -> {
            itemService.updateItem(anyLong(), "test", any(RequestUpdateItem.class));
        }).isInstanceOf(CustomIllegalArgumentException.class).hasMessageContaining(ERROR_NOT_FOUND_ITEM.getMessage());
    }
}