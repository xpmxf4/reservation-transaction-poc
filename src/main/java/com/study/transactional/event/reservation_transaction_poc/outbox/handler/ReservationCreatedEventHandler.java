package com.study.transactional.event.reservation_transaction_poc.outbox.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.transactional.event.reservation_transaction_poc.booking.entity.Reservation;
import com.study.transactional.event.reservation_transaction_poc.booking.service.event.ReservationCreatedEvent;
import com.study.transactional.event.reservation_transaction_poc.ums.enums.TemplateCode;
import com.study.transactional.event.reservation_transaction_poc.ums.service.UmsPropagationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationCreatedEventHandler implements SpecificEventHandler {

    private final UmsPropagationService umsPropagationService;
    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(String eventType) {
        return "RESERVED".equals(eventType);
    }

    @Override
    public void handle(String payload) {
        try {
            log.info("Handling RESERVED event with payload: {}", payload);

            ReservationCreatedEvent event = objectMapper.readValue(payload, ReservationCreatedEvent.class);

            umsPropagationService.sendAlimtalk(event.userPhone(),
                    TemplateCode.RESERVATED,
                    event.productId()
            );

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to handler RESERVED event", e);
        }

    }
}
