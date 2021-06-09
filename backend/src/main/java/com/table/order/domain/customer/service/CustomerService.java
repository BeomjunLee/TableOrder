package com.table.order.domain.customer.service;

import com.table.order.domain.customer.dto.request.RequestLoginCustomer;
import com.table.order.domain.customer.dto.response.ResponseLoginCustomer;
import com.table.order.domain.customer.entity.Customer;
import com.table.order.domain.customer.repository.CustomerQueryRepository;
import com.table.order.domain.customer.repository.CustomerRepository;
import com.table.order.domain.table.entity.Table;
import com.table.order.domain.table.repository.TableQueryRepository;
import com.table.order.global.common.exception.CustomIllegalArgumentException;
import com.table.order.global.security.provider.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.table.order.global.common.code.CustomErrorCode.ERROR_NOT_FOUND_TABLE_STORE;
import static com.table.order.global.common.code.ResultCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final TableQueryRepository tableQueryRepository;
    private final CustomerQueryRepository customerQueryRepository;
    private final JwtProvider jwtProvider;

    /**
     * QR 코드 스캔 후 회원가입 + 로그인
     * @param requestLoginCustomer 클라이언트 요청 form
     * @return 응답 dto
     */
    public ResponseLoginCustomer scanQrCode(RequestLoginCustomer requestLoginCustomer) {

        Optional<Customer> optionalCustomer = customerQueryRepository.findByUsernameJoinStore(requestLoginCustomer);
        Table findTable = validateCustomerAndTableStore(requestLoginCustomer);

        if (optionalCustomer.isPresent()) {
            Customer findCustomer = optionalCustomer.get();

            if(findCustomer.isInUse()) {
                return ResponseLoginCustomer.builder()
                        .status(RESULT_RE_LOGIN.getStatus())
                        .message(RESULT_RE_LOGIN.getMessage())
                        .accessToken(jwtProvider.generateToken(requestLoginCustomer))
                        .expiredAt(LocalDateTime.now().plusSeconds(jwtProvider.getAccessTokenValidMilliSeconds()/1000))
                        .build();
            }
            if (findCustomer.isVisited()) {
                findCustomer.updateTable(findTable);
                return ResponseLoginCustomer.builder()
                        .status(RESULT_RE_VISIT.getStatus())
                        .message(RESULT_RE_VISIT.getMessage())
                        .accessToken(jwtProvider.generateToken(requestLoginCustomer))
                        .expiredAt(LocalDateTime.now().plusSeconds(jwtProvider.getAccessTokenValidMilliSeconds()/1000))
                        .build();
            }
        }

        Customer customer = Customer.createCustomer(requestLoginCustomer.getUsername(), findTable, findTable.getStore());
        customerRepository.save(customer);

        return ResponseLoginCustomer.builder()
                .status(RESULT_CUSTOMER_SIGN_UP.getStatus())
                .message(RESULT_CUSTOMER_SIGN_UP.getMessage())
                .accessToken(jwtProvider.generateToken(requestLoginCustomer))
                .expiredAt(LocalDateTime.now().plusSeconds(jwtProvider.getAccessTokenValidMilliSeconds()/1000))
                .build();
    }

    private Table validateCustomerAndTableStore(RequestLoginCustomer dto) {
        return tableQueryRepository.findTableJoinStore(dto.getTableId(), dto.getStoreId())
                    .orElseThrow(() -> new CustomIllegalArgumentException(ERROR_NOT_FOUND_TABLE_STORE.getErrorCode(), ERROR_NOT_FOUND_TABLE_STORE.getMessage()));
    }

}
