package com.table.order.domain.customer.repository;

import com.table.order.domain.customer.entity.Customer;
import com.table.order.domain.customer.entity.CustomerStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Long countByUsernameAndTableIdAndStoreIdAndCustomerStatus(String username, Long tableId, Long storeId, CustomerStatus customerStatus);
}
