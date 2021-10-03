package com.table.order.domain.item.service;

import com.table.order.domain.item.dto.ItemDto;
import com.table.order.domain.item.dto.request.RequestAddItem;
import com.table.order.domain.item.dto.request.RequestUpdateItem;
import com.table.order.domain.item.dto.response.ResponseAddItem;
import com.table.order.domain.item.entity.Item;
import com.table.order.domain.item.repository.ItemQueryRepository;
import com.table.order.domain.item.repository.ItemRepository;
import com.table.order.domain.store.entity.Store;
import com.table.order.domain.store.repository.StoreQueryRepository;
import com.table.order.global.common.code.ResultCode;
import com.table.order.global.common.exception.CustomIllegalArgumentException;
import com.table.order.global.common.response.ResponseResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.table.order.global.common.code.CustomErrorCode.ERROR_NOT_FOUND_ITEM;
import static com.table.order.global.common.code.CustomErrorCode.ERROR_NOT_FOUND_STORE;
import static com.table.order.global.common.code.ResultCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemQueryRepository itemQueryRepository;
    private final StoreQueryRepository storeQueryRepository;

    /**
     * 메뉴 추가
     * @param requestAddItem 메뉴 추가 form
     * @param username 회원 아이디
     * @return 응답 dto
     */
    public ResponseAddItem addItem(RequestAddItem requestAddItem, String username) {
        Store findStore = storeQueryRepository.findByUsernameJoinUserCategory(username)
                .orElseThrow(() -> new CustomIllegalArgumentException(ERROR_NOT_FOUND_STORE.getErrorCode(), ERROR_NOT_FOUND_STORE.getMessage()));

        Item item = Item.createItem(requestAddItem, findStore);
        Item savedItem = itemRepository.save(item);

        ItemDto dto = ItemDto.builder()
                .id(savedItem.getId())
                .name(savedItem.getName())
                .description(savedItem.getDescription())
                .price(savedItem.getPrice())
                .image(savedItem.getImage())
                .build();

        return ResponseAddItem.builder()
                .status(RESULT_ADD_ITEM.getStatus())
                .message(RESULT_ADD_ITEM.getMessage())
                .data(dto)
                .build();
    }

    /**
     * 메뉴 삭제
     * @param itemId 메뉴 고유 id
     * @param username 회원 아이디
     * @return 응답 dto
     */
    public ResponseResult deleteItem(Long itemId, String username) {
        itemQueryRepository.findByIdJoinStoreUser(itemId, username)
                .orElseThrow(() -> new CustomIllegalArgumentException(ERROR_NOT_FOUND_ITEM.getErrorCode(), ERROR_NOT_FOUND_ITEM.getMessage()));

        itemRepository.deleteById(itemId);

        return ResponseResult.builder()
                .status(RESULT_DELETE_ITEM.getStatus())
                .message(RESULT_DELETE_ITEM.getMessage())
                .build();
    }

    /**
     * 메뉴 수정
     * @param itemId 메뉴 고유 id
     * @param username 회원 아이디
     * @param requestUpdateItem 수정 form
     * @return 응답 dto
     */
    public ResponseResult updateItem(Long itemId, String username, RequestUpdateItem requestUpdateItem) {
        Item findItem = itemQueryRepository.findByIdJoinStoreUser(itemId, username)
                .orElseThrow(() -> new CustomIllegalArgumentException(ERROR_NOT_FOUND_ITEM.getErrorCode(), ERROR_NOT_FOUND_ITEM.getMessage()));

        findItem.updateItem(requestUpdateItem);

        return ResponseResult.builder()
                .status(RESULT_UPDATE_ITEM.getStatus())
                .message(RESULT_UPDATE_ITEM.getMessage())
                .build();
    }
}
