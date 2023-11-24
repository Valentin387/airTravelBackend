package com.laboratory.airlinebackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "tblShoppingCartSeats")
public class ShoppingCartSeats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long ID;

    private long shoppingCartID;
    private long seatID;
    private double unitPrice;

}
