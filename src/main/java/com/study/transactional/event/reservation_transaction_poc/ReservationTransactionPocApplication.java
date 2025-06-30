package com.study.transactional.event.reservation_transaction_poc;

import com.study.transactional.event.reservation_transaction_poc.booking.config.SnsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@EnableConfigurationProperties({SnsProperties.class})
@SpringBootApplication
public class ReservationTransactionPocApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservationTransactionPocApplication.class, args);
	}

}
