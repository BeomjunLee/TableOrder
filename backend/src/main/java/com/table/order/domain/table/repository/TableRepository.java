package com.table.order.domain.table.repository;

import com.table.order.domain.table.entity.Table;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableRepository extends JpaRepository<Table, Long> {
}
