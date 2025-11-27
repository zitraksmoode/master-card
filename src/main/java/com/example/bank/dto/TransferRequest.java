package com.example.bank.dto;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record TransferRequest(
        Long fromCardId,
        Long toCardId,
        @Positive(message = "Сумма должна быть положительной") BigDecimal amount
) {}