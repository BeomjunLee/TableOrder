package com.table.order.domain.table.service;

import com.table.order.domain.store.entity.Store;
import com.table.order.domain.store.repository.StoreQueryRepository;
import com.table.order.domain.table.dto.TableDto;
import com.table.order.domain.table.dto.request.RequestAddTable;
import com.table.order.domain.table.dto.response.ResponseAddTable;
import com.table.order.domain.table.entity.Table;
import com.table.order.domain.table.repository.TableRepository;
import com.table.order.global.common.code.ResultCode;
import com.table.order.global.common.exception.CustomIllegalArgumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.table.order.global.common.code.CustomErrorCode.ERROR_NOT_FOUND_STORE;
import static com.table.order.global.common.code.ResultCode.RESULT_ADD_TABLE;

@Service
@RequiredArgsConstructor
public class TableService {

    private final TableRepository tableRepository;
    private final StoreQueryRepository storeQueryRepository;

    /**
     * 테이블 추가
     * @param requestAddTable 클라이언트 요청 form
     * @param username 회원 이름
     * @return 응답 dto
     */
    public ResponseAddTable addTable(RequestAddTable requestAddTable, String username) {
        Store findStore = storeQueryRepository.findByUsernameJoinUser(username)
                .orElseThrow(() -> new CustomIllegalArgumentException(ERROR_NOT_FOUND_STORE.getErrorCode(), ERROR_NOT_FOUND_STORE.getMessage()));

        Table table = Table.createTable(requestAddTable, findStore);
        Table savedTable = tableRepository.save(table);

        TableDto dto = TableDto.builder()
                .id(savedTable.getId())
                .name(savedTable.getName())
                .numberOfPeople(savedTable.getNumberOfPeople())
                .tableStatus(savedTable.getTableStatus())
                .build();

        return ResponseAddTable.builder()
                .status(RESULT_ADD_TABLE.getStatus())
                .message(RESULT_ADD_TABLE.getMessage())
                .data(dto)
                .build();
    }
}
