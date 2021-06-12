package com.table.order.domain.customer;

import com.table.order.domain.customer.dto.request.RequestLoginCustomer;
import com.table.order.domain.customer.dto.response.ResponseLoginCustomer;
import com.table.order.domain.customer.entity.Customer;
import com.table.order.domain.customer.entity.CustomerStatus;
import com.table.order.domain.customer.repository.CustomerQueryRepository;
import com.table.order.domain.customer.repository.CustomerRepository;
import com.table.order.domain.customer.service.CustomerService;
import com.table.order.domain.store.entity.Store;
import com.table.order.domain.store.entity.StoreStatus;
import com.table.order.domain.store.exception.CustomAccessDeniedException;
import com.table.order.domain.table.entity.Table;
import com.table.order.domain.table.entity.TableStatus;
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

import java.time.LocalDateTime;
import java.util.Optional;

import static com.table.order.global.common.code.CustomErrorCode.*;
import static com.table.order.global.common.code.ResultCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    @InjectMocks
    private CustomerService customerService;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CustomerQueryRepository customerQueryRepository;
    @Mock
    private TableQueryRepository tableQueryRepository;
    @Mock
    private JwtProvider jwtProvider;

    private RequestLoginCustomer requestLoginCustomer;
    private ResponseLoginCustomer responseLoginCustomer;
    private Customer customer;
    private Store store;
    private Table table;

    @BeforeEach
    public void init() {
        requestLoginCustomer = RequestLoginCustomer.builder()
                .username("test")
                .tableId(1L)
                .storeId(2L)
                .build();

        responseLoginCustomer = ResponseLoginCustomer.builder()
                .status(RESULT_SIGN_UP_CUSTOMER.getStatus())
                .message(RESULT_SIGN_UP_CUSTOMER.getMessage())
                .accessToken("(accessToken)")
                .expiredAt(LocalDateTime.now().plusSeconds(1800))
                .build();

        store = Store.builder()
                .name("식당")
                .description("식당 설명")
                .licenseImage("이미지 주소")
                .storeStatus(StoreStatus.VALID)
                .build();

        table = Table.builder()
                .name("테이블")
                .numberOfPeople(5)
                .tableStatus(TableStatus.OPEN)
                .store(store)
                .build();

        customer = Customer.createCustomer(requestLoginCustomer.getUsername(), table, store);
    }
    
    @Test
    @DisplayName("QR 코드 스캔 테스트 (회원가입)")
    public void scanQrCode() throws Exception{
        //given
        given(customerQueryRepository.findByUsernameJoinStore(any(RequestLoginCustomer.class)))
                .willReturn(Optional.ofNullable(null));
        given(tableQueryRepository.findTableJoinStore(anyLong(), anyLong())).willReturn(Optional.of(table));
        given(customerRepository.save(any(Customer.class))).willReturn(customer);
        given(jwtProvider.generateToken(any(RequestLoginCustomer.class))).willReturn("(accessToken)");

        //when
        ResponseLoginCustomer response = customerService.scanQrCode(requestLoginCustomer);

        //then
        assertThat(response).extracting("status", "message", "accessToken")
                .containsExactly(responseLoginCustomer.getStatus(), responseLoginCustomer.getMessage(), responseLoginCustomer.getAccessToken());
    }

    @Test
    @DisplayName("QR 코드 스캔 테스트 (재로그인)")
    public void validateCustomer() throws Exception{
        //given
        given(jwtProvider.generateToken(any(RequestLoginCustomer.class))).willReturn("(accessToken)");
        given(customerQueryRepository.findByUsernameJoinStore(any(RequestLoginCustomer.class))).willReturn(Optional.ofNullable(customer));
        given(tableQueryRepository.findTableJoinStore(anyLong(), anyLong())).willReturn(Optional.of(table));

        responseLoginCustomer = ResponseLoginCustomer.builder()
                .status(RESULT_RE_LOGIN.getStatus())
                .message(RESULT_RE_LOGIN.getMessage())
                .accessToken("(accessToken)")
                .expiredAt(LocalDateTime.now().plusSeconds(1800))
                .build();
        //when
        ResponseLoginCustomer response = customerService.scanQrCode(requestLoginCustomer);

        //then
        assertThat(response).extracting("status", "message", "accessToken")
                .containsExactly(responseLoginCustomer.getStatus(), responseLoginCustomer.getMessage(), responseLoginCustomer.getAccessToken());
    }

    @Test
    @DisplayName("QR 코드 스캔 테스트 (재방문)")
    public void validateCustomerReVisit() throws Exception{
        //given
        int visitCount = customer.getVisitCount();
        customer = Customer.builder()
                .username(requestLoginCustomer.getUsername())
                .visitCount(++visitCount)
                .customerStatus(CustomerStatus.OUT)
                .table(table)
                .store(store)
                .build();

        given(jwtProvider.generateToken(any(RequestLoginCustomer.class))).willReturn("(accessToken)");
        given(customerQueryRepository.findByUsernameJoinStore(any(RequestLoginCustomer.class))).willReturn(Optional.ofNullable(customer));
        given(tableQueryRepository.findTableJoinStore(anyLong(), anyLong())).willReturn(Optional.ofNullable(table));

        responseLoginCustomer = ResponseLoginCustomer.builder()
                .status(RESULT_RE_VISIT.getStatus())
                .message(RESULT_RE_VISIT.getMessage())
                .accessToken("(accessToken)")
                .expiredAt(LocalDateTime.now().plusSeconds(1800))
                .build();
        //when
        ResponseLoginCustomer response = customerService.scanQrCode(requestLoginCustomer);

        //then
        assertThat(response).extracting("status", "message", "accessToken")
                .containsExactly(responseLoginCustomer.getStatus(), responseLoginCustomer.getMessage(), responseLoginCustomer.getAccessToken());
    }

    @Test
    @DisplayName("테이블 식당 검증 실패")
    public void validateTableStore() throws Exception{
        //given
        given(customerQueryRepository.findByUsernameJoinStore(any(RequestLoginCustomer.class))).willReturn(Optional.ofNullable(customer));
        given(tableQueryRepository.findTableJoinStore(requestLoginCustomer.getTableId(), requestLoginCustomer.getStoreId())).willReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> {
            customerService.scanQrCode(requestLoginCustomer);
        }).isInstanceOf(CustomIllegalArgumentException.class).hasMessageContaining(ERROR_NOT_FOUND_TABLE_STORE.getMessage());
    }

    @Test
    @DisplayName("QR 코드 스캔 실패 테스트 (테이블 상태가 OPEN 이 아닐 때)")
    public void scanQrCodeTableStatusIsNotOpen() throws Exception{
        //given
        table = Table.builder()
                .name("테이블")
                .numberOfPeople(5)
                .tableStatus(TableStatus.ORDER)
                .store(store)
                .build();

        customer = Customer.builder()
                .username(requestLoginCustomer.getUsername())
                .visitCount(1)
                .customerStatus(CustomerStatus.IN)
                .table(table)
                .store(store)
                .build();

        given(customerQueryRepository.findByUsernameJoinStore(any(RequestLoginCustomer.class))).willReturn(Optional.ofNullable(customer));
        given(tableQueryRepository.findTableJoinStore(anyLong(), anyLong())).willReturn(Optional.of(table));

        //when then
        assertThatThrownBy(() -> {
            customerService.scanQrCode(requestLoginCustomer);
        }).isInstanceOf(CustomAccessDeniedException.class).hasMessageContaining(ERROR_IN_USE_TABLE.getMessage());
    }

    @Test
    @DisplayName("QR 코드 스캔 실패 테스트 (식당이 미승인 상태일 때)")
    public void scanQrCodeStoreStatusIsInvalid() throws Exception{
        //given
        store = Store.builder()
                .name("식당")
                .description("식당 설명")
                .licenseImage("이미지 주소")
                .storeStatus(StoreStatus.INVALID)
                .build();

        customer = Customer.builder()
                .username(requestLoginCustomer.getUsername())
                .visitCount(1)
                .customerStatus(CustomerStatus.IN)
                .table(table)
                .store(store)
                .build();

        given(customerQueryRepository.findByUsernameJoinStore(any(RequestLoginCustomer.class))).willReturn(Optional.ofNullable(customer));
        given(tableQueryRepository.findTableJoinStore(anyLong(), anyLong())).willReturn(Optional.of(table));

        //when then
        assertThatThrownBy(() -> {
            customerService.scanQrCode(requestLoginCustomer);
        }).isInstanceOf(CustomAccessDeniedException.class).hasMessageContaining(ERROR_INVALID_STORE.getMessage());
    }


}