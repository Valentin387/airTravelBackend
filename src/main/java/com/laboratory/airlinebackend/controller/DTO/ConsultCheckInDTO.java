package com.laboratory.airlinebackend.controller.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultCheckInDTO {

    private long flightId;
    private long passengerId;
    private String email;
    private String firstName;
    private String lastName;
    private String DNI;
    private boolean didCheckIn;
    private String origin;
    private String destination;
    private Date flightDate;
    private String state;
    private long SeatId;
    private int SeatNumber;
    private char SeatLetter;

}

