package com.study.transactional.event.reservation_transaction_poc.booking.service.ums;

import com.study.transactional.event.reservation_transaction_poc.booking.event.ReservationCreatedEvent;

public interface BookingEventPublisher {

    void publishReservationCreatedEvent(ReservationCreatedEvent event);

//    void publishReservationUpdatedEvent(ReservationUpdatedEvent event);

//    void publishReservationCancelledEvent(ReservationCancelledEvent event);
}
