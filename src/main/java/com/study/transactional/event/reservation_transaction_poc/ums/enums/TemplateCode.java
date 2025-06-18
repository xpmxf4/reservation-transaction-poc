package com.study.transactional.event.reservation_transaction_poc.ums.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TemplateCode {
    RESERVATED("CODE-RESERVATED"),
    CANCELLED("CODE-CANCELLED");

    private final String code;
}
