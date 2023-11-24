package com.laboratory.airlinebackend.repository;

import com.laboratory.airlinebackend.model.Card;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findById(long cardId);

    Optional<Card> findByNumber(String number);


    Card getCardById(long cardId);
}
