package com.table.order.domain.category.dto;

import com.table.order.domain.item.dto.ItemDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryDto {
    private Long id;
    private String name;
    private List<ItemDto> items;


    @Builder
    public CategoryDto(Long id, String name, List<ItemDto> items) {
        this.id = id;
        this.name = name;
        this.items = items;
    }
}
