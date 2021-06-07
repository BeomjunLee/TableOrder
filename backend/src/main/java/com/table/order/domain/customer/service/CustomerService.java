package com.table.order.domain.customer.service;

import com.table.order.domain.customer.dto.request.RequestCustomerLogin;
import com.table.order.domain.customer.entity.Customer;
import com.table.order.domain.customer.entity.CustomerStatus;
import com.table.order.domain.customer.exception.AlreadyInUseService;
import com.table.order.domain.customer.repository.CustomerRepository;
import com.table.order.domain.table.entity.Table;
import com.table.order.domain.table.repository.TableQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final TableQueryRepository tableQueryRepository;

    public Customer scanQrCode(RequestCustomerLogin requestCustomerLogin) {

        Table table = validateCustomerAndTableStore(requestCustomerLogin);

        Customer customer = Customer.createCustomer(requestCustomerLogin.getUsername(), table, table.getStore());
        return customerRepository.save(customer);
    }

    private Table validateCustomerAndTableStore(RequestCustomerLogin dto) {

        if(customerRepository.countByUsernameAndTableIdAndStoreIdAndCustomerStatus(dto.getUsername(), dto.getTableId(), dto.getStoreId(), CustomerStatus.IN) > 0)
            throw new AlreadyInUseService("로그인 성공");

        return tableQueryRepository.findTableJoinStore(dto.getTableId(), dto.getStoreId())
                    .orElseThrow(() -> new IllegalArgumentException("테이블 또는 식당을 찾을 수 없습니다"));
    }

}
