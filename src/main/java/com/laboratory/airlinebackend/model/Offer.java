package com.laboratory.airlinebackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "tblOffer")
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long ID;
    private double discount;
    @Column(length = 150)
    private String description;
    @Column(length = 100)
    private String origin;
    @Column(length = 100)
    private String destination;
    private Date validDateRange;
    /*va a tocar decir, que si la fecha actual es menor a la fecha
     de la oferta, entonces se puede mostrar la oferta (tipo, fecha límite)
     */
}
