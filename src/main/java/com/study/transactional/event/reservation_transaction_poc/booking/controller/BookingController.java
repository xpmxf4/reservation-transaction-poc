package com.study.transactional.event.reservation_transaction_poc.booking.controller;

import com.study.transactional.event.reservation_transaction_poc.booking.service.BookingPropagationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequiredArgsConstructor
public class BookingController {

    private final BookingPropagationService bookingPropagationService;

    @GetMapping("/propagation/test")
    public String bookingPropagationTest(@RequestParam String userNo, @RequestParam String productId) {
        bookingPropagationService.createReservationWithUmsRequiresNewTakesLongTime(Long.valueOf(userNo), productId, new ArrayList<>());

        return "Booking test completed successfully.";
    }

}
