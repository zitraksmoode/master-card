package com.example.bank.controller;

import com.example.bank.dto.TransferRequest;
import com.example.bank.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TransferController {

    private final CardService cardService;

    @PostMapping("/transfers")
    public ResponseEntity<String> transfer(@Valid @RequestBody TransferRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        cardService.transfer(request, username);
        return ResponseEntity.ok("Перевод выполнен успешно");
    }
}