package com.table.order.domain.customer.repository;

import com.table.order.domain.customer.entity.Customer;
import com.table.order.domain.customer.entity.CustomerStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
