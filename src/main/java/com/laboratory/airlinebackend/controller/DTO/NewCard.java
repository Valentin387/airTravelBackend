package com.laboratory.airlinebackend.controller.DTO;

import com.laboratory.airlinebackend.model.Card;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewCard {
    private double balance;
    private String type;
    private String number;
    private String name;
    private String expirationDate;
    private String cvc;
}
