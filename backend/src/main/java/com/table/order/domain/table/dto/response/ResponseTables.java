package com.table.order.domain.table.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.table.order.domain.table.dto.TableDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResponseTables {
    private int status;
    private String message;
    private Page<TableDto> data;

    @Builder
    public ResponseTables(int status, String message, Page<TableDto> data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
