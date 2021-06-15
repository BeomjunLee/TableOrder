package com.table.order.domain.category.service;

import com.table.order.domain.category.dto.CategoryDto;
import com.table.order.domain.category.dto.request.RequestAddCategory;
import com.table.order.domain.category.dto.response.ResponseAddCategory;
import com.table.order.domain.category.dto.response.ResponseCategoriesItems;
import com.table.order.domain.category.entity.Category;
import com.table.order.domain.category.repository.CategoryQueryRepository;
import com.table.order.domain.category.repository.CategoryRepository;
import com.table.order.domain.item.dto.ItemDto;
import com.table.order.domain.store.entity.Store;
import com.table.order.domain.store.repository.StoreQueryRepository;
import com.table.order.global.common.exception.CustomIllegalArgumentException;
import com.table.order.global.common.response.ResponseResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.table.order.global.common.code.CustomErrorCode.*;
import static com.table.order.global.common.code.ResultCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryQueryRepository categoryQueryRepository;
    private final StoreQueryRepository storeQueryRepository;

    /**
     * 카테고리 추가
     * @param requestAddCategory 카테고리 추가 form
     * @param username 회원 아이디
     * @return 응답 dto
     */
    public ResponseAddCategory addCategory(RequestAddCategory requestAddCategory, String username) {
        Store findStore = storeQueryRepository.findByUsernameJoinUser(username)
                .orElseThrow(() -> new CustomIllegalArgumentException(ERROR_NOT_FOUND_STORE.getErrorCode(), ERROR_NOT_FOUND_STORE.getMessage()));

        Category category = Category.createCategory(requestAddCategory, findStore);
        Category savedCategory = categoryRepository.save(category);

        CategoryDto dto = CategoryDto.builder()
                .id(savedCategory.getId())
                .name(savedCategory.getName())
                .build();

        return ResponseAddCategory.builder()
                .status(RESULT_ADD_CATEGORY.getStatus())
                .message(RESULT_ADD_CATEGORY.getMessage())
                .data(dto)
                .build();
    }

    /**
     * 전체 카테고리 보기 (카테고리별 메뉴까지) -회원-
     * @param username 회원 아이디
     * @return 응답 dto
     */
    public ResponseCategoriesItems findCategoriesUser(String username) {
        List<Category> findCategories = categoryQueryRepository.findAllJoinUserStoreItem(username);

        return transferCategoriesToDto(findCategories);
    }

    /**
     * 전체 카테고리 보가 (카테고리별 메뉴까지) -손님-
     * @param username 손님 아이디
     * @return 응답 dto
     */
    public ResponseCategoriesItems findCategoriesCustomer(String username) {
        List<Category> findCategories = categoryQueryRepository.findAllJoinCustomerStoreItem(username);

        return transferCategoriesToDto(findCategories);
    }

    /**
     * 카테고리 리스트 -> DTO
     * @param findCategories 카테고리 리스트
     * @return DTO
     */
    private ResponseCategoriesItems transferCategoriesToDto(List<Category> findCategories) {
        List<CategoryDto> categoryDtos = findCategories.stream().map(category -> CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .items(category.getItems().stream().map(item -> ItemDto.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .description(item.getDescription())
                        .price(item.getPrice())
                        .image(item.getImage())
                        .build())
                        .collect(Collectors.toList()))
                .build())
                .collect(Collectors.toList());

        return ResponseCategoriesItems.builder()
                .status(RESULT_FIND_CATEGORIES_ITEMS.getStatus())
                .message(RESULT_FIND_CATEGORIES_ITEMS.getMessage())
                .data(categoryDtos)
                .build();
    }

    /**
     * 카테고리 삭제 (삭제시 관련 item 들도 다 삭제)
     * @param categoryId 카테고리 고유 id
     * @param username 회원 아이디
     * @return 응답 dto
     */
    public ResponseResult deleteCategory(Long categoryId, String username) {
        Category findCategory = categoryQueryRepository.findByIdJoinStoreUser(categoryId, username)
                .orElseThrow(() -> new CustomIllegalArgumentException(ERROR_DELETE_CATEGORY.getErrorCode(), ERROR_DELETE_CATEGORY.getMessage()));

        categoryRepository.delete(findCategory);
        return ResponseResult.builder()
                .status(RESULT_DELETE_CATEGORY.getStatus())
                .message(RESULT_DELETE_CATEGORY.getMessage())
                .build();
    }
}
