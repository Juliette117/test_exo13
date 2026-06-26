package org.example.bank.account.dto;

import java.math.BigDecimal;

public record MoneyRequest(BigDecimal amount) {
}
