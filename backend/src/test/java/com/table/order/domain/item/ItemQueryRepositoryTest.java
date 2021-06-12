package com.table.order.domain.item;
import com.table.order.domain.item.dto.OrderItemDto;
import com.table.order.domain.item.entity.Item;
import com.table.order.domain.item.repository.ItemQueryRepository;
import com.table.order.domain.item.repository.ItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ItemQueryRepositoryTest {
    @Autowired
    private ItemQueryRepository itemQueryRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("여러가지 메뉴를 한번에 주문할 때의 동적 쿼리 테스트")
    public void findAllByItemIds() throws Exception{
        //given
        List<OrderItemDto> orderItemDtos = new ArrayList<>();
        for (long i = 1; i <= 5 ; i++) {
            Item item = Item.builder()
                    .name("메뉴" + i)
                    .description("메뉴 설명")
                    .build();
            Item savedItem = itemRepository.save(item);

            OrderItemDto dto = OrderItemDto.builder()
                    .id(savedItem.getId())
                    .build();
            orderItemDtos.add(dto);
        }

        //when
        em.flush();
        em.clear();
        List<Item> findItems = itemQueryRepository.findAllByItemIds(orderItemDtos);
        assertThat(findItems.size()).isEqualTo(5);

        orderItemDtos.remove(0);
        orderItemDtos.remove(1);
        List<Item> findItems2 = itemQueryRepository.findAllByItemIds(orderItemDtos);
        //then
        assertThat(findItems2.size()).isEqualTo(3);
    }
}