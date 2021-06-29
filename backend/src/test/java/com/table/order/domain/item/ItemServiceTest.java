package com.table.order.domain.item;
import com.table.order.domain.category.entity.Category;
import com.table.order.domain.item.dto.ItemDto;
import com.table.order.domain.item.dto.request.RequestAddItem;
import com.table.order.domain.item.dto.response.ResponseAddItem;
import com.table.order.domain.item.entity.Item;
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
import static com.table.order.global.common.code.CustomErrorCode.ERROR_INVALID_STORE;
import static com.table.order.global.common.code.CustomErrorCode.ERROR_NOT_FOUND_STORE;
import static com.table.order.global.common.code.ResultCode.RESULT_ADD_ITEM;
import static com.table.order.global.common.code.ResultCode.RESULT_DELETE_ITEM;
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

    private RequestAddItem requestAddItem;
    private ResponseAddItem responseAddItem;
    private ItemDto itemDto;
    private Item item;
    private Store store;
    private Category category;

    @BeforeEach
    public void init() {
        requestAddItem = RequestAddItem.builder()
                .name("메뉴 1")
                .description("메뉴 설명")
                .price(1000)
                .image("이미지")
                .categoryId(null)
                .build();

        itemDto = ItemDto.builder()
                .id(null)
                .name("메뉴 1")
                .description("메뉴 설명")
                .price(1000)
                .image("이미지")
                .build();

        responseAddItem = ResponseAddItem.builder()
                .status(RESULT_ADD_ITEM.getStatus())
                .message(RESULT_ADD_ITEM.getMessage())
                .data(itemDto)
                .build();

        store = Store.builder()
                .name("식당")
                .description("식당 설명")
                .licenseImage("이미지 주소")
                .storeStatus(StoreStatus.VALID)
                .build();

        category = Category.builder()
                .name("카테고리 1")
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
    @DisplayName("메뉴 추가 테스트")
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
    @DisplayName("메뉴 추가 실패 테스트 (미승인된 식당")
    public void addItemInvalidStore() throws Exception{
        //given
        store = Store.builder()
                .name("식당")
                .description("식당 설명")
                .licenseImage("이미지 주소")
                .storeStatus(StoreStatus.INVALID)
                .build();

        given(storeQueryRepository.findByUsernameJoinUserCategory(anyString())).willReturn(Optional.ofNullable(store));

        //when then
        assertThatThrownBy(() -> {
            itemService.addItem(requestAddItem, anyString());
        }).isInstanceOf(CustomConflictException.class).hasMessageContaining(ERROR_INVALID_STORE.getMessage());
    }

    @Test
    @DisplayName("메뉴 추가 실패 테스트 (식당을 찾을 수 없음")
    public void addItemNotFoundStore() throws Exception{
        //given
        given(storeQueryRepository.findByUsernameJoinUserCategory(anyString())).willReturn(Optional.ofNullable(null));

        //when then
        assertThatThrownBy(() -> {
            itemService.addItem(requestAddItem, anyString());
        }).isInstanceOf(CustomIllegalArgumentException.class).hasMessageContaining(ERROR_NOT_FOUND_STORE.getMessage());
    }

    @Test
    @DisplayName("메뉴 삭제 테스트")
    public void deleteItem() throws Exception{
        //given
        ResponseResult responseResult = ResponseResult.builder()
                .status(RESULT_DELETE_ITEM.getStatus())
                .message(RESULT_DELETE_ITEM.getMessage())
                .build();

        given(storeQueryRepository.findByUsernameJoinUser(anyString())).willReturn(Optional.of(store));

        //when
        ResponseResult response = itemService.deleteItem(anyLong(), "test");

        //then
        assertThat(response).extracting("status", "message")
                .containsExactly(responseResult.getStatus(), responseResult.getMessage());
    }

    @Test
    @DisplayName("메뉴 삭제 실패 테스트 (식당을 찾을 수 없음")
    public void deleteItemNotFoundStore() throws Exception{
        //given
        given(storeQueryRepository.findByUsernameJoinUser(anyString())).willReturn(Optional.ofNullable(null));

        //when then
        assertThatThrownBy(() -> {
            itemService.deleteItem(anyLong(), "test");
        }).isInstanceOf(CustomIllegalArgumentException.class).hasMessageContaining(ERROR_NOT_FOUND_STORE.getMessage());
    }

}