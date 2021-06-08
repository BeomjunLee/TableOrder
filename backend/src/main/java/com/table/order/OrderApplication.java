package com.table.order;

import com.table.order.domain.store.entity.Store;
import com.table.order.domain.store.repository.StoreRepository;
import com.table.order.domain.table.entity.Table;
import com.table.order.domain.table.entity.TableStatus;
import com.table.order.domain.table.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class OrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderApplication.class, args);
	}
}
