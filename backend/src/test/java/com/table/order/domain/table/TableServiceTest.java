package com.table.order.domain.table;

import com.table.order.domain.store.entity.Store;
import com.table.order.domain.store.entity.StoreStatus;
import com.table.order.domain.store.exception.CustomAccessDeniedException;
import com.table.order.domain.store.repository.StoreQueryRepository;
import com.table.order.domain.table.dto.TableDto;
import com.table.order.domain.table.dto.request.RequestAddTable;
import com.table.order.domain.table.dto.response.ResponseAddTable;
import com.table.order.domain.table.entity.Table;
import com.table.order.domain.table.entity.TableStatus;
import com.table.order.domain.table.repository.TableRepository;
import com.table.order.domain.table.service.TableService;
import com.table.order.global.common.code.CustomErrorCode;
import com.table.order.global.common.code.ResultCode;
import com.table.order.global.common.exception.CustomIllegalArgumentException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.table.order.global.common.code.CustomErrorCode.ERROR_INVALID_STORE;
import static com.table.order.global.common.code.CustomErrorCode.ERROR_NOT_FOUND_STORE;
import static com.table.order.global.common.code.ResultCode.RESULT_ADD_TABLE;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TableServiceTest {
    @InjectMocks
    private TableService tableService;
    @Mock
    private TableRepository tableRepository;
    @Mock
    private StoreQueryRepository storeQueryRepository;

    private RequestAddTable requestAddTable;
    private ResponseAddTable responseAddTable;
    private Table table;
    private Store store;
    private TableDto tableDto;

    @BeforeEach
    public void init() {
        requestAddTable = RequestAddTable.builder()
                .name("테이블 1")
                .numberOfPeople(5)
                .build();

        tableDto = TableDto.builder()
                .id(null)
                .name("테이블 1")
                .numberOfPeople(5)
                .tableStatus(TableStatus.OPEN)
                .build();

        responseAddTable = ResponseAddTable.builder()
                .status(RESULT_ADD_TABLE.getStatus())
                .message(RESULT_ADD_TABLE.getMessage())
                .data(tableDto)
                .build();

        store = Store.builder()
                .name("식당")
                .description("식당 설명")
                .licenseImage("사업자 등록증")
                .storeStatus(StoreStatus.VALID)
                .user(null)
                .build();

        table = Table.createTable(requestAddTable, store);
    }
    
    @Test
    @DisplayName("테이블 추가 테스트")
    public void addTable() throws Exception{
        //given
        given(storeQueryRepository.findByUsernameJoinUser(anyString())).willReturn(Optional.ofNullable(store));
        given(tableRepository.save(any(Table.class))).willReturn(table);

        //when
        ResponseAddTable response = tableService.addTable(requestAddTable, anyString());

        //then
        assertThat(response).extracting("status", "message", "data.id", "data.name", "data.numberOfPeople", "data.tableStatus")
                .containsExactly(
                        responseAddTable.getStatus(),
                        responseAddTable.getMessage(),
                        responseAddTable.getData().getId(),
                        responseAddTable.getData().getName(),
                        responseAddTable.getData().getNumberOfPeople(),
                        responseAddTable.getData().getTableStatus());
    }

    @Test
    @DisplayName("테이블 추가 실패 테스트 (승인되지 않은 식당)")
    public void addTableInvalidStore() throws Exception{
        //given
        store = Store.builder()
                .name("식당")
                .description("식당 설명")
                .licenseImage("사업자 등록증")
                .storeStatus(StoreStatus.INVALID)
                .user(null)
                .build();

        given(storeQueryRepository.findByUsernameJoinUser(anyString())).willReturn(Optional.ofNullable(store));

        //when then
        assertThatThrownBy(() -> {
            tableService.addTable(requestAddTable, anyString());
        }).isInstanceOf(CustomAccessDeniedException.class).hasMessageContaining(ERROR_INVALID_STORE.getMessage());
    }

    @Test
    @DisplayName("테이블 추가 테스트 (식당을 찾을 수 없음)")
    public void addTableNotFoundStore() throws Exception{
        //given
        given(storeQueryRepository.findByUsernameJoinUser(anyString())).willReturn(Optional.ofNullable(null));

        //when then
        assertThatThrownBy(() -> {
            tableService.addTable(requestAddTable, anyString());
        }).isInstanceOf(CustomIllegalArgumentException.class).hasMessageContaining(ERROR_NOT_FOUND_STORE.getMessage());
    }
}