package com.laboratory.airlinebackend.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "tblOrder")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long ID;

    @Column(length = 100)
    private String COD;

    private long userID;
    private long shoppingCartID;
    private long cardID;
    private double totalAmount;
    private Date date;

    @Column(length = 100)
    private String paymentDetails;

}
