package com.table.order.domain.customer.service;

import com.table.order.domain.customer.dto.request.RequestCustomerLogin;
import com.table.order.domain.customer.dto.response.ResponseLogin;
import com.table.order.domain.customer.entity.CustomerStatus;
import com.table.order.domain.customer.repository.CustomerRepository;
import com.table.order.domain.table.repository.TableQueryRepository;
import com.table.order.global.common.exception.CustomIllegalArgumentException;
import com.table.order.global.security.provider.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static com.table.order.global.common.code.CustomErrorCode.ERROR_NOT_FOUND_TABLE_STORE;
import static com.table.order.global.common.code.ResultCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CustomerServiceTest {
    @InjectMocks
    private CustomerService customerService;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private TableQueryRepository tableQueryRepository;
    @Mock
    private JwtProvider jwtProvider;

    private RequestCustomerLogin customerLogin;

    @BeforeEach
    public void init() {
        customerLogin = RequestCustomerLogin.builder()
                .username("test")
                .tableId(1L)
                .storeId(2L)
                .build();
    }

    @Test
    @DisplayName("Customer 검증 (존재시 재 로그인)")
    public void validateCustomer() throws Exception{
        //given
        given(jwtProvider.generateToken(customerLogin)).willReturn("accessToken");
        given(customerRepository.countByUsernameAndTableIdAndStoreIdAndCustomerStatus(
                customerLogin.getUsername(), customerLogin.getTableId(), customerLogin.getStoreId(), CustomerStatus.IN)).willReturn(1L);

        //when
        ResponseLogin responseLogin = customerService.scanQrCode(customerLogin);

        //then
        assertThat(responseLogin.getAccessToken()).isEqualTo("accessToken");
        assertThat(responseLogin.getMessage()).isEqualTo(RESULT_RE_LOGIN.getMessage());
    }

    @Test
    @DisplayName("테이블 식당 검증")
    public void validateTableStore() throws Exception{
        //given
        given(customerRepository.countByUsernameAndTableIdAndStoreIdAndCustomerStatus(
                customerLogin.getUsername(), customerLogin.getTableId(), customerLogin.getStoreId(), CustomerStatus.IN)).willReturn(0L);
        given(tableQueryRepository.findTableJoinStore(customerLogin.getTableId(), customerLogin.getStoreId())).willReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> {
            customerService.scanQrCode(customerLogin);
        }).isInstanceOf(CustomIllegalArgumentException.class).hasMessageContaining(ERROR_NOT_FOUND_TABLE_STORE.getMessage());
    }
}