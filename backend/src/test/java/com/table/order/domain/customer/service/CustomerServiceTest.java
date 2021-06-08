package com.table.order.domain.customer.service;

import com.table.order.domain.customer.dto.request.RequestCustomerLogin;
import com.table.order.domain.customer.entity.Customer;
import com.table.order.domain.customer.entity.CustomerStatus;
import com.table.order.domain.customer.exception.AlreadyInUseService;
import com.table.order.domain.customer.repository.CustomerRepository;
import com.table.order.domain.table.entity.Table;
import com.table.order.domain.table.repository.TableQueryRepository;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CustomerServiceTest {
    @InjectMocks
    private CustomerService customerService;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private TableQueryRepository tableQueryRepository;

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
        when(customerRepository.countByUsernameAndTableIdAndStoreIdAndCustomerStatus(
                customerLogin.getUsername(), customerLogin.getTableId(), customerLogin.getStoreId(), CustomerStatus.IN)).thenReturn(1L);

        //when then
        assertThatThrownBy(() -> {
            customerService.scanQrCode(customerLogin);
        }).isInstanceOf(AlreadyInUseService.class).hasMessageContaining("로그인 성공");
    }

    @Test
    @DisplayName("테이블 식당 검증")
    public void validateTableStore() throws Exception{
        //given
        when(customerRepository.countByUsernameAndTableIdAndStoreIdAndCustomerStatus(
                customerLogin.getUsername(), customerLogin.getTableId(), customerLogin.getStoreId(), CustomerStatus.IN)).thenReturn(0L);
        when(tableQueryRepository.findTableJoinStore(customerLogin.getTableId(), customerLogin.getStoreId())).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> {
            customerService.scanQrCode(customerLogin);
        }).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("테이블 또는 식당을 찾을 수 없습니다");
    }
}