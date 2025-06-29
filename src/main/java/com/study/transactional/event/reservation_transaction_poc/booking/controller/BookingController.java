package com.study.transactional.event.reservation_transaction_poc.booking.controller;

import com.study.transactional.event.reservation_transaction_poc.booking.service.BookingAndEventPublishService;
import com.study.transactional.event.reservation_transaction_poc.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/booking")
public class BookingController {

    private final BookingService bookingService;
    private final BookingAndEventPublishService bookingAndEventPublishService;

    // 예약 생성이라 POST 가 맞지만, 테스트 용도로 GET 으로 사용
    @GetMapping
    public String createBooking(@RequestParam String userNo, @RequestParam String productId) {
        bookingService.createBooking(Long.valueOf(userNo), productId);

        return "Booking created successfully.";
    }

}
