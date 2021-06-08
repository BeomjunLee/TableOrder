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
import static com.table.order.global.common.code.ResultCode.RE_LOGIN;
import static com.table.order.global.common.code.ResultCode.SIGN_UP;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final TableQueryRepository tableQueryRepository;
    private final JwtProvider jwtProvider;

    /**
     * QR 코드 스캔 후 회원가입 + 로그인
     * @param requestCustomerLogin 클라이언트 요청 dto
     * @return 응답 dto
     */
    public ResponseLogin scanQrCode(RequestCustomerLogin requestCustomerLogin) {
        String accessToken = jwtProvider.generateToken(requestCustomerLogin);

        if(isInUseCustomer(requestCustomerLogin))
            return ResponseLogin.builder()
                    .status(RE_LOGIN.getStatus())
                    .message(RE_LOGIN.getMessage())
                    .accessToken(accessToken)
                    .expiredAt(LocalDateTime.now().plusSeconds(jwtProvider.getAccessTokenValidMilliSeconds()/1000))
                    .build();

        Table table = validateCustomerAndTableStore(requestCustomerLogin);

        Customer customer = Customer.createCustomer(requestCustomerLogin.getUsername(), table, table.getStore());
        customerRepository.save(customer);

        return ResponseLogin.builder()
                .status(SIGN_UP.getStatus())
                .message(SIGN_UP.getMessage())
                .accessToken(accessToken)
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
