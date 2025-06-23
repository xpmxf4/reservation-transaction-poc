package com.study.transactional.event.reservation_transaction_poc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication
public class ReservationTransactionPocApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservationTransactionPocApplication.class, args);
	}

}
