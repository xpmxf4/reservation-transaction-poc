package com.study.transactional.event.reservation_transaction_poc.booking.publisher;


import com.study.transactional.event.reservation_transaction_poc.booking.event.BookingCreatedEvent;

public interface BookingEventPublisher {

    void publishReservationCreatedEvent(BookingCreatedEvent event);

//    void publishReservationUpdatedEvent(ReservationUpdatedEvent event);

//    void publishReservationCancelledEvent(ReservationCancelledEvent event);
}
