package com.table.order.domain.category.service;

import com.table.order.domain.category.dto.CategoryDto;
import com.table.order.domain.category.dto.request.RequestAddCategory;
import com.table.order.domain.category.dto.response.ResponseAddCategory;
import com.table.order.domain.category.entity.Category;
import com.table.order.domain.category.repository.CategoryRepository;
import com.table.order.domain.store.entity.Store;
import com.table.order.domain.store.repository.StoreQueryRepository;
import com.table.order.global.common.code.ResultCode;
import com.table.order.global.common.exception.CustomIllegalArgumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.table.order.global.common.code.CustomErrorCode.ERROR_NOT_FOUND_STORE;
import static com.table.order.global.common.code.ResultCode.RESULT_ADD_CATEGORY;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
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
}
