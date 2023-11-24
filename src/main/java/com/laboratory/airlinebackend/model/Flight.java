package com.laboratory.airlinebackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "tblFlight")
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 150)
    private String image;

    @Column
    private Date flightDate;

    @Column(length = 100)
    private String origin;

    @Column(name = "destination", length = 100)
    private String destination;

    @Column(name = "flightDuration", length = 50)
    private String flightDuration;

    @Column
    private Date arrivalDate;

    @Column
    private double costByPerson;

    @Column
    private double costByPersonOffer;

    @Column
    private int availableSeats;

    @Column(length = 50)
    private String state;

    @Column(length = 150)
    private String cancelationMessage;

    @Column
    private boolean isInternational;

    @Column
    private long offerID;



    // Constructors, getters, setters, and other methods

}
