package com.laboratory.airlinebackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "tblCard")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private double balance;

    @Column(length = 15)
    private String type;

    @Column(length = 16)
    private String number;

    @Column(length = 25)
    private String name;

    @Column(length = 4)
    private String expirationDate;

    @Column(length = 3)
    private String cvc;

    @JsonIgnore
    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CardUser> cardUsers;


}
