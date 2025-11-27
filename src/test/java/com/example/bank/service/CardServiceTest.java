package com.example.bank.service;

import com.example.bank.dto.TransferRequest;
import com.example.bank.entity.Card;
import com.example.bank.entity.CardStatus;
import com.example.bank.entity.User;
import com.example.bank.repository.CardRepository;
import com.example.bank.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CardServiceTest {

    @InjectMocks
    private CardService cardService;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    private User user;
    private Card fromCard;
    private Card toCard;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setOwner(user);
        fromCard.setBalance(BigDecimal.valueOf(1000));
        fromCard.setStatus(CardStatus.ACTIVE);

        toCard = new Card();
        toCard.setId(2L);
        toCard.setOwner(user);
        toCard.setBalance(BigDecimal.ZERO);
        toCard.setStatus(CardStatus.ACTIVE);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));
        when(cardRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
    }

    @Test
    void transfer_Success() {
        TransferRequest request = new TransferRequest(1L, 2L, BigDecimal.valueOf(300));

        cardService.transfer(request, "testuser");

        assertEquals(BigDecimal.valueOf(700), fromCard.getBalance());
        assertEquals(BigDecimal.valueOf(300), toCard.getBalance());
        verify(cardRepository, times(2)).save(any(Card.class));
    }

    @Test
    void transfer_InsufficientFunds_ThrowsException() {
        TransferRequest request = new TransferRequest(1L, 2L, BigDecimal.valueOf(2000));

        assertThrows(RuntimeException.class, () -> cardService.transfer(request, "testuser"));
    }

    @Test
    void transfer_DifferentOwner_ThrowsException() {
        toCard.setOwner(new User());

        TransferRequest request = new TransferRequest(1L, 2L, BigDecimal.valueOf(100));

        assertThrows(RuntimeException.class, () -> cardService.transfer(request, "testuser"));
    }
}