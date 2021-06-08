package com.table.order.domain.store.service;

import com.table.order.domain.store.dto.StoreDto;
import com.table.order.domain.store.dto.request.RequestEnrollStore;
import com.table.order.domain.store.dto.response.ResponseEnrollStore;
import com.table.order.domain.store.entity.Store;
import com.table.order.domain.store.repository.StoreRepository;
import com.table.order.domain.user.entity.User;
import com.table.order.domain.user.repository.UserRepository;
import com.table.order.global.common.exception.CustomIllegalArgumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.table.order.global.common.code.CustomErrorCode.ERROR_NOT_FOUND_USER;
import static com.table.order.global.common.code.ResultCode.RESULT_ENROLL_STORE;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    /**
     * 식당 생성
     * @param requestEnrollStore 클라이언트 요청 form
     * @param username 회원 아이디
     * @return 응답 dto
     */
    public ResponseEnrollStore createStore(RequestEnrollStore requestEnrollStore, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomIllegalArgumentException(ERROR_NOT_FOUND_USER.getErrorCode(), ERROR_NOT_FOUND_USER.getMessage()));
        Store store = Store.createStore(requestEnrollStore, user);
        Store savedStore = storeRepository.save(store);

        StoreDto dto = StoreDto.builder()
                .id(savedStore.getId())
                .name(savedStore.getName())
                .description(savedStore.getDescription())
                .build();

        return ResponseEnrollStore.builder()
                .status(RESULT_ENROLL_STORE.getStatus())
                .message(RESULT_ENROLL_STORE.getMessage())
                .data(dto)
                .build();
    }

}
