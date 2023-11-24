package com.laboratory.airlinebackend.model;

import jakarta.persistence.*;
import lombok.*;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "tblCardUser")
public class CardUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "userID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "cardID")
    private Card card;

    @Column
    private int quantityCreditCards;

    @Column
    private int quantityDebitCards;
}


