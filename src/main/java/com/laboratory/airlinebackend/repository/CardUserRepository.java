package com.laboratory.airlinebackend.repository;


import com.laboratory.airlinebackend.model.Card;
import com.laboratory.airlinebackend.model.CardUser;
import com.laboratory.airlinebackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CardUserRepository extends JpaRepository<CardUser, Long> {
    List<CardUser> findByUser(User existingUser);

    List<CardUser> findByUserAndCardId(User existingUser, long id);

    int countByUserAndQuantityCreditCardsGreaterThan(User existingUser, int i);

    int countByUserAndQuantityDebitCardsGreaterThan(User existingUser, int i);

    @Query("SELECT cu.card FROM tblCardUser cu JOIN cu.card card WHERE cu.user.ID = :userId")
    List<Card> findCardsByUser(@Param("userId") Long userId);

}

