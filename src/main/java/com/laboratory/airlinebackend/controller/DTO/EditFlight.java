package com.laboratory.airlinebackend.controller.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditFlight {

        private String image;
        private double costByPerson;
        private double costByPersonOffer;
        private int availableSeats;
        private String state;
}
