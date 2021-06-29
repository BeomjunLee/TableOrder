package com.table.order.domain.item.controller;

import com.table.order.domain.item.dto.request.RequestAddItem;
import com.table.order.domain.item.dto.response.ResponseAddItem;
import com.table.order.domain.item.service.ItemService;
import com.table.order.global.common.response.ResponseResult;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping("")
    @ResponseStatus(CREATED)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseAddItem addItem(@RequestBody RequestAddItem requestAddItem,
                                   Authentication authentication) {
        return itemService.addItem(requestAddItem, authentication.getName());
    }

    @DeleteMapping("/{itemId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseResult deleteItem(@PathVariable Long itemId,
                                     Authentication authentication) {
        return itemService.deleteItem(itemId, authentication.getName());
    }
}
