package com.table.order;
import com.table.order.domain.store.entity.Store;
import com.table.order.domain.store.entity.StoreStatus;
import com.table.order.domain.store.repository.StoreRepository;
import com.table.order.domain.table.entity.Table;
import com.table.order.domain.table.entity.TableStatus;
import com.table.order.domain.table.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class OrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderApplication.class, args);
	}

//	@Autowired
//	private StoreRepository storeRepository;
//	@Autowired
//	private TableRepository tableRepository;
//
//	@PostConstruct
//	public void init() {
//		Store store = Store.builder()
//				.name("식당")
//				.description("식당 설명")
//				.licenseImage("이미지 주소")
//				.storeStatus(StoreStatus.VALID)
//				.build();
//
//		Table table = Table.builder()
//				.name("테이블")
//				.numberOfPeople(5)
//				.tableStatus(TableStatus.OPEN)
//				.store(store)
//				.build();
//
//		Table table2 = Table.builder()
//				.name("테이블2")
//				.numberOfPeople(5)
//				.tableStatus(TableStatus.OPEN)
//				.store(store)
//				.build();
//
//		storeRepository.save(store);
//		tableRepository.save(table);
//		tableRepository.save(table2);
//	}
}
