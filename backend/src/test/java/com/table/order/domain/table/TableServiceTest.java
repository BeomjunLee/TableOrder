package com.table.order.domain.table;

import com.table.order.domain.item.entity.Item;
import com.table.order.domain.order.dto.OrderDto;
import com.table.order.domain.order.entity.Order;
import com.table.order.domain.order.entity.OrderStatus;
import com.table.order.domain.store.entity.Store;
import com.table.order.domain.store.entity.StoreStatus;
import com.table.order.domain.store.exception.CustomAccessDeniedException;
import com.table.order.domain.store.repository.StoreQueryRepository;
import com.table.order.domain.table.dto.TableDto;
import com.table.order.domain.table.dto.request.RequestAddTable;
import com.table.order.domain.table.dto.response.ResponseAddTable;
import com.table.order.domain.table.dto.response.ResponseTables;
import com.table.order.domain.table.entity.Table;
import com.table.order.domain.table.entity.TableStatus;
import com.table.order.domain.table.repository.TableQueryRepository;
import com.table.order.domain.table.repository.TableRepository;
import com.table.order.domain.table.service.TableService;
import com.table.order.global.common.exception.CustomIllegalArgumentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.table.order.global.common.code.CustomErrorCode.ERROR_INVALID_STORE;
import static com.table.order.global.common.code.CustomErrorCode.ERROR_NOT_FOUND_STORE;
import static com.table.order.global.common.code.ResultCode.RESULT_ADD_TABLE;
import static com.table.order.global.common.code.ResultCode.RESULT_FIND_TABLES;
import static org.assertj.core.api.Assertions.*;
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
    @Mock
    private TableQueryRepository tableQueryRepository;

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
                .totalPrice(0)
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
    
    @Test
    @DisplayName("테이블 전체 조회 테스트")
    public void findTables() throws Exception{
        //given
        List<OrderDto> orderDtos = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            OrderDto orderDto = OrderDto.builder()
                    .orderPrice(i * 5000)
                    .name("메뉴"+i)
                    .count(i)
                    .request("잘부탁드립니다")
                    .orderStatus(OrderStatus.ORDER)
                    .build();
            orderDtos.add(orderDto);
        }
        List<TableDto> tableDtos = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            TableDto tableDto = TableDto.builder()
                    .name("테이블"+i)
                    .numberOfPeople(5)
                    .tableStatus(TableStatus.OPEN)
                    .orders(orderDtos)
                    .build();
            tableDtos.add(tableDto);
        }
        Pageable pageable = PageRequest.of(0, 10);
        Page<TableDto> pageResultDto = new PageImpl<TableDto>(tableDtos, pageable, 2);

        List<Table> tables = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            Table table = Table.builder()
                    .name("테이블"+i)
                    .numberOfPeople(5)
                    .tableStatus(TableStatus.OPEN)
                    .build();
            for (int j = 1; j <= 2 ; j++) {
                Order order = Order.builder()
                        .orderPrice(j * 5000)
                        .count(j)
                        .request("잘부탁드립니다")
                        .orderStatus(OrderStatus.ORDER)
                        .item(Item.builder().name("메뉴"+j).build())
                        .build();

                table.getOrders().add(order);
            }
            tables.add(table);
        }
        Page<Table> pageResultTable = new PageImpl<Table>(tables, pageable, 2);

        given(tableQueryRepository.findAllJoinStoreUserOrder("test", pageable)).willReturn(pageResultTable);

        ResponseTables responseTables = ResponseTables.builder()
                .status(RESULT_FIND_TABLES.getStatus())
                .message(RESULT_FIND_TABLES.getMessage())
                .data(pageResultDto)
                .build();

        //when
        ResponseTables response = tableService.findTables("test", pageable);

        //then
        assertThat(response).usingRecursiveComparison().isEqualTo(responseTables);
    }
}