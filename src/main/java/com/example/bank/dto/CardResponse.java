package com.example.bank.dto;

import com.example.bank.entity.CardStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CardResponse(
        Long id,
        String maskedCardNumber,
        String holderName,
        LocalDate expiryDate,
        CardStatus status,
        BigDecimal balance
) {}