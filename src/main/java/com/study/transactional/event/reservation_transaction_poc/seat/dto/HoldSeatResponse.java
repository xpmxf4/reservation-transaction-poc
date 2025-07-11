package com.study.transactional.event.reservation_transaction_poc.seat.dto;

public record HoldSeatResponse(boolean isSuccess, Long seatHoldId) {

}
