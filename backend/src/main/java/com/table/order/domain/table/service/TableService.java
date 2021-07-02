package com.table.order.domain.table.service;

import com.table.order.domain.order.dto.OrderDto;
import com.table.order.domain.order.entity.Order;
import com.table.order.domain.order.entity.OrderStatus;
import com.table.order.domain.store.entity.Store;
import com.table.order.domain.store.repository.StoreQueryRepository;
import com.table.order.domain.table.dto.TableDto;
import com.table.order.domain.table.dto.request.RequestAddTable;
import com.table.order.domain.table.dto.request.RequestUpdateTable;
import com.table.order.domain.table.dto.response.ResponseAddTable;
import com.table.order.domain.table.dto.response.ResponseTables;
import com.table.order.domain.table.entity.Table;
import com.table.order.domain.table.repository.TableQueryRepository;
import com.table.order.domain.table.repository.TableRepository;
import com.table.order.global.common.exception.CustomIllegalArgumentException;
import com.table.order.global.common.response.ResponseResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

import static com.table.order.global.common.code.CustomErrorCode.ERROR_NOT_FOUND_STORE;
import static com.table.order.global.common.code.CustomErrorCode.ERROR_NOT_FOUND_TABLE_STORE;
import static com.table.order.global.common.code.ResultCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class TableService {

    private final TableRepository tableRepository;
    private final TableQueryRepository tableQueryRepository;
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

    /**
     * 테이블 전체 조회
     * @param username 회원 아이디
     * @param pageable 페이징
     * @return 응답 dto
     */
    @Transactional(readOnly = true)
    public ResponseTables findTables(String username, Pageable pageable) {
        Page<Table> tables = tableQueryRepository.findAllJoinStoreUserOrder(username, OrderStatus.ORDER, pageable);

        Page<TableDto> results = tables.map(table -> TableDto.builder()
                .id(table.getId())
                .name(table.getName())
                .numberOfPeople(table.getNumberOfPeople())
                .tableStatus(table.getTableStatus())
                .orders(table.getOrders().stream().map(order -> OrderDto.builder()
                        .id(order.getId())
                        .name(order.getItem().getName())
                        .orderPrice(order.getOrderPrice())
                        .count(order.getCount())
                        .request(order.getRequest())
                        .orderStatus(order.getOrderStatus())
                        .build())
                        .collect(Collectors.toList()))
                .totalPrice(table.getOrders().stream().mapToInt(order -> order.getOrderPrice()).sum())
                .build());

        return ResponseTables.builder()
                .status(RESULT_FIND_TABLES.getStatus())
                .message(RESULT_FIND_TABLES.getMessage())
                .data(results)
                .build();
    }

    /**
     * 테이블 수정
     * @param tableId 테이블 고유 id
     * @param username 회원 아이디
     * @param requestUpdateTable 테이블 수정 form
     * @return 응답 dto
     */
    public ResponseResult updateTable(Long tableId, String username, RequestUpdateTable requestUpdateTable) {
        Table table = tableQueryRepository.findByIdJoinStoreUser(tableId, username)
                .orElseThrow(() -> new CustomIllegalArgumentException(ERROR_NOT_FOUND_TABLE_STORE.getErrorCode(), ERROR_NOT_FOUND_TABLE_STORE.getMessage()));
        table.updateTable(requestUpdateTable);

        return ResponseResult.builder()
                .status(RESULT_UPDATE_TABLE.getStatus())
                .message(RESULT_UPDATE_TABLE.getMessage())
                .build();
    }

    //TODO 테이블 오픈 (테이블 초기화)
    //TODO 계산 완료
    //TODO 회원들도 테이블 보기 (계산서)
    //TODO 테이블 삭제
}
