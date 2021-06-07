package com.table.order.domain.log.repository;

import com.table.order.domain.log.entity.TableLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableLogRepository extends JpaRepository<TableLog, Long> {
}
