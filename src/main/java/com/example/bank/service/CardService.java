package com.example.bank.service;

import com.example.bank.dto.CardResponse;
import com.example.bank.dto.CreateCardRequest;
import com.example.bank.entity.Card;
import com.example.bank.entity.User;
import com.example.bank.repository.CardRepository;
import com.example.bank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public CardResponse createCard(CreateCardRequest request, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Card card = new Card();
        card.setCardNumber(request.cardNumber());
        card.setHolderName(request.holderName());
        card.setExpiryDate(LocalDate.parse(request.expiryDate() + "-01"));
        card.setOwner(owner);

        card = cardRepository.save(card);
        return toResponse(card);
    }

    @Transactional(readOnly = true)
    public Page<CardResponse> getMyCards(Pageable pageable) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return cardRepository.findByOwnerId(user.getId(), pageable)
                .map(this::toResponse);
    }

    private CardResponse toResponse(Card card) {
        return new CardResponse(
                card.getId(),
                card.getMaskedCardNumber(),
                card.getHolderName(),
                card.getExpiryDate(),
                card.getStatus(),
                card.getBalance()
        );
    }
}