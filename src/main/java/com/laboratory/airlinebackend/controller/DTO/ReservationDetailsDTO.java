package com.laboratory.airlinebackend.controller.DTO;

import com.laboratory.airlinebackend.model.Seat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDetailsDTO {
    private Date reservationDate;
    private Date expirationDate;
    private Long flightId;
    private String origin;
    private String destination;
    private Date flightDate;
    private String state;
    private Double costByPerson;
    private Double costByPersonOffer;
    private List<Seat> seats;
}
