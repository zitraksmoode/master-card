package com.example.bank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record CreateCardRequest(
        @NotBlank @Pattern(regexp = "\\d{16}") String cardNumber,
        @NotBlank @Size(max = 100) String holderName,
        @NotBlank String expiryDate
) {}