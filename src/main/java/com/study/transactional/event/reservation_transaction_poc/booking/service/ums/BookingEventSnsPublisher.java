package com.study.transactional.event.reservation_transaction_poc.booking.service.ums;

import com.study.transactional.event.reservation_transaction_poc.booking.event.ReservationCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;

@Component
@RequiredArgsConstructor
public class BookingEventSnsPublisher implements BookingEventPublisher {

    private final SnsClient snsClient;

    @Override
    public void publishReservationCreatedEvent(ReservationCreatedEvent event) {


    }
}
