package com.table.order.domain.store.controller;

import com.table.order.domain.store.dto.request.RequestEnrollStore;
import com.table.order.domain.store.dto.response.ResponseEnrollStore;
import com.table.order.domain.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stores")
public class StoreController {

    private final StoreService storeService;

    @PostMapping("")
    @ResponseStatus(CREATED)
//    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEnrollStore createStore(@RequestBody RequestEnrollStore requestEnrollStore,
                                           Principal principal) {
        return storeService.createStore(requestEnrollStore, principal.getName());
    }
}
