package org.example.bank.account.dto;

import java.math.BigDecimal;

public record TransferRequest(String sourceNumber, String targetNumber, BigDecimal amount) {
}
