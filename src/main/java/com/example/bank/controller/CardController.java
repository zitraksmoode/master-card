package com.example.bank.controller;

import com.example.bank.dto.CardResponse;
import com.example.bank.dto.CreateCardRequest;
import com.example.bank.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    // Только админ
    @PostMapping("/admin/cards")
    public ResponseEntity<CardResponse> createCard(
            @Valid @RequestBody CreateCardRequest request,
            @RequestParam Long ownerId) {
        return ResponseEntity.ok(cardService.createCard(request, ownerId));
    }

    // Все авторизованные — свои карты
    @GetMapping("/cards/me")
    public ResponseEntity<Page<CardResponse>> getMyCards(Pageable pageable) {
        return ResponseEntity.ok(cardService.getMyCards(pageable));
    }
    @PostMapping("/cards/{id}/request-block")
    public ResponseEntity<String> requestBlock(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        cardService.requestBlock(id, username);
        return ResponseEntity.ok("Запрос на блокировку отправлен");
    }

    @PostMapping("/admin/cards/{id}/block")
    public ResponseEntity<String> blockCard(@PathVariable Long id) {
        cardService.approveBlock(id);
        return ResponseEntity.ok("Карта заблокирована");
    }
}