package com.laboratory.airlinebackend.controller.DTO;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestFlight {
    private Date flightDate;
    private String origin;
    private String destination;
    private String flightDuration;
    private Date arrivalDate;
    private double costByPerson;

}
