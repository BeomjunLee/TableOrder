package com.table.order.domain.customer.service;

import com.table.order.domain.customer.dto.request.RequestCustomerLogin;
import com.table.order.domain.customer.dto.response.ResponseLogin;
import com.table.order.domain.customer.entity.Customer;
import com.table.order.domain.customer.entity.CustomerStatus;
import com.table.order.domain.customer.repository.CustomerRepository;
import com.table.order.domain.table.entity.Table;
import com.table.order.domain.table.repository.TableQueryRepository;
import com.table.order.global.common.exception.CustomIllegalArgumentException;
import com.table.order.global.security.provider.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.table.order.global.common.code.CustomErrorCode.ERROR_NOT_FOUND_TABLE_STORE;
import static com.table.order.global.common.code.ResultCode.RESULT_RE_LOGIN;
import static com.table.order.global.common.code.ResultCode.RESULT_SIGN_UP;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final TableQueryRepository tableQueryRepository;
    private final JwtProvider jwtProvider;

    /**
     * QR 코드 스캔 후 회원가입 + 로그인
     * @param requestCustomerLogin 클라이언트 요청 form
     * @return 응답 dto
     */
    public ResponseLogin scanQrCode(RequestCustomerLogin requestCustomerLogin) {
        if(isInUseCustomer(requestCustomerLogin))
            return ResponseLogin.builder()
                    .status(RESULT_RE_LOGIN.getStatus())
                    .message(RESULT_RE_LOGIN.getMessage())
                    .accessToken(jwtProvider.generateToken(requestCustomerLogin))
                    .expiredAt(LocalDateTime.now().plusSeconds(jwtProvider.getAccessTokenValidMilliSeconds()/1000))
                    .build();

        Table table = validateCustomerAndTableStore(requestCustomerLogin);

        Customer customer = Customer.createCustomer(requestCustomerLogin.getUsername(), table, table.getStore());
        customerRepository.save(customer);

        return ResponseLogin.builder()
                .status(RESULT_SIGN_UP.getStatus())
                .message(RESULT_SIGN_UP.getMessage())
                .accessToken(jwtProvider.generateToken(requestCustomerLogin))
                .expiredAt(LocalDateTime.now().plusSeconds(jwtProvider.getAccessTokenValidMilliSeconds()/1000))
                .build();
    }

    private Table validateCustomerAndTableStore(RequestCustomerLogin dto) {
        return tableQueryRepository.findTableJoinStore(dto.getTableId(), dto.getStoreId())
                    .orElseThrow(() -> new CustomIllegalArgumentException(ERROR_NOT_FOUND_TABLE_STORE.getErrorCode(), ERROR_NOT_FOUND_TABLE_STORE.getMessage()));
    }

    private boolean isInUseCustomer(RequestCustomerLogin dto) {
        return customerRepository.countByUsernameAndTableIdAndStoreIdAndCustomerStatus(dto.getUsername(), dto.getTableId(), dto.getStoreId(), CustomerStatus.IN) > 0;
    }
    
}
