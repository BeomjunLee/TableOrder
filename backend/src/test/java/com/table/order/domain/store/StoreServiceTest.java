package com.table.order.domain.store;

import com.table.order.domain.store.dto.StoreDto;
import com.table.order.domain.store.dto.request.RequestEnrollStore;
import com.table.order.domain.store.dto.response.ResponseEnrollStore;
import com.table.order.domain.store.entity.Store;
import com.table.order.domain.store.entity.StoreStatus;
import com.table.order.domain.store.repository.StoreRepository;
import com.table.order.domain.store.service.StoreService;
import com.table.order.domain.user.entity.User;
import com.table.order.domain.user.entity.UserRole;
import com.table.order.domain.user.repository.UserRepository;
import com.table.order.global.common.code.CustomErrorCode;
import com.table.order.global.common.exception.CustomIllegalArgumentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static com.table.order.global.common.code.CustomErrorCode.ERROR_NOT_FOUND_USER;
import static com.table.order.global.common.code.ResultCode.RESULT_ENROLL_STORE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {
    @InjectMocks
    private StoreService storeService;
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private UserRepository userRepository;

    private User user;
    private Store store;
    private StoreDto storeDto;
    private RequestEnrollStore requestEnrollStore;
    private ResponseEnrollStore responseEnrollStore;

    @BeforeEach
    public void init() {
        user = User.builder()
                .username("beomjun")
                .password("1234")
                .name("이범준")
                .phoneNum("01012345678")
                .address("서울시 강남구 0000")
                .userRole(UserRole.USER)
                .build();

        store = Store.builder()
                .name("식당")
                .description("식당 설명")
                .licenseImage("이미지 주소")
                .storeStatus(StoreStatus.VALID)
                .build();

        requestEnrollStore = RequestEnrollStore.builder()
                .name("식당")
                .description("식당 설명")
                .licenseImage("이미지 주소")
                .build();

        storeDto = StoreDto.builder()
                .id(null)
                .name("식당")
                .description("식당 설명")
                .licenseImage("이미지 주소")
                .build();

        responseEnrollStore = ResponseEnrollStore.builder()
                .status(RESULT_ENROLL_STORE.getStatus())
                .message(RESULT_ENROLL_STORE.getMessage())
                .data(storeDto)
                .build();
    }

    @Test
    @DisplayName("식당 생성 테스트")
    public void createStore() throws Exception{
        //given
        given(userRepository.findByUsername(anyString())).willReturn(Optional.ofNullable(user));
        given(storeRepository.save(any(Store.class))).willReturn(store);

        //when
        ResponseEnrollStore response = storeService.createStore(requestEnrollStore, anyString());

        //then
        assertThat(response).extracting("status", "message", "data.id", "data.name", "data.description", "data.licenseImage")
                .containsExactly(
                        responseEnrollStore.getStatus(),
                        responseEnrollStore.getMessage(),
                        responseEnrollStore.getData().getId(),
                        responseEnrollStore.getData().getName(),
                        responseEnrollStore.getData().getDescription(),
                        responseEnrollStore.getData().getLicenseImage()
                );
    }

    @Test
    @DisplayName("식당 생성 실패 테스트 (회원 아이디 찾기 실패)")
    public void createStoreNotFoundUsername() throws Exception{
        //given
        given(userRepository.findByUsername(anyString())).willReturn(Optional.ofNullable(null));

        //when then
        assertThatThrownBy(() -> {
            storeService.createStore(requestEnrollStore, anyString());
        }).isInstanceOf(CustomIllegalArgumentException.class).hasMessageContaining(ERROR_NOT_FOUND_USER.getMessage());


    }
}