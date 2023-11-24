package com.laboratory.airlinebackend.controller.DTO;

import com.laboratory.airlinebackend.model.Passenger;
import com.laboratory.airlinebackend.model.Seat;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailsDTO {
    private Long flightId;
    private String origin;
    private String destination;
    private Date flightDate;
    private String state;
    private Double costByPerson;
    private Double costByPersonOffer;
    private List<Passenger> passengers;
}
