package com.study.transactional.event.reservation_transaction_poc.booking.controller;

import com.study.transactional.event.reservation_transaction_poc.booking.dto.CreateBooking;
import com.study.transactional.event.reservation_transaction_poc.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/booking")
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public String createBooking(@RequestAttribute Long userNo, @RequestAttribute String companyId, @RequestParam String productId, @RequestParam String productDetailId) {
        CreateBooking createBooking = new CreateBooking(
            userNo,
            companyId,
            productId,
            productDetailId
        );
        String result = bookingService.createBooking(createBooking);

        return String.format("Booking created: %s", result);
    }

}
