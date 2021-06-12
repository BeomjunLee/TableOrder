package com.table.order.domain.table.controller;

import com.table.order.domain.table.dto.request.RequestAddTable;
import com.table.order.domain.table.dto.response.ResponseAddTable;
import com.table.order.domain.table.dto.response.ResponseTables;
import com.table.order.domain.table.service.TableService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tables")
public class TableController {

    private final TableService tableService;

    @PostMapping("")
    @ResponseStatus(CREATED)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseAddTable addTable(@RequestBody RequestAddTable requestAddTable,
                                     Authentication authentication) {
        return tableService.addTable(requestAddTable, authentication.getName());
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseTables findTables(Pageable pageable, Authentication authentication) {
        return tableService.findTables(authentication.getName(), pageable);
    }
}
