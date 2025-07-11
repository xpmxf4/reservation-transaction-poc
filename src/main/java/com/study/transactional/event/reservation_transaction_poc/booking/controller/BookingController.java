package com.study.transactional.event.reservation_transaction_poc.booking.controller;

import com.study.transactional.event.reservation_transaction_poc.booking.dto.BookingResponse;
import com.study.transactional.event.reservation_transaction_poc.booking.dto.ClientCreateBookingRequestDto;
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
    public String createBooking(@RequestAttribute Long userNo, @RequestAttribute Long companyId, @RequestParam String productId, @RequestParam String productDetailId) {
        ClientCreateBookingRequestDto clientCreateBookingRequestDto = new ClientCreateBookingRequestDto(userNo, companyId, productId, productDetailId);
        BookingResponse bookingResponse = bookingService.createBooking(clientCreateBookingRequestDto);

        return bookingResponse.isSuccess() ? "예약 성공: " + bookingResponse.bookingId() : "예약 실패: " + bookingResponse.message();
    }

}
