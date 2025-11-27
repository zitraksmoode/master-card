package com.example.bank.repository;

import com.example.bank.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
    Page<Card> findByOwnerId(Long ownerId, Pageable pageable);
}