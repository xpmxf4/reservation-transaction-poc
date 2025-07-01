package com.study.transactional.event.reservation_transaction_poc.publisher.domain.booking.sns;


import com.study.transactional.event.reservation_transaction_poc.publisher.domain.booking.outbox.event.dto.BookingCreatedEvent;

public interface BookingEventPublisher {

    void publishReservationCreatedEvent(BookingCreatedEvent event);

//    void publishReservationUpdatedEvent(ReservationUpdatedEvent event);

//    void publishReservationCancelledEvent(ReservationCancelledEvent event);
}
