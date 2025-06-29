package com.study.transactional.event.reservation_transaction_poc.booking.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TemplateCode {
    RESERVED("CODE-RESERVED"),
    CANCELLED("CODE-CANCELLED");

    private final String code;
}
