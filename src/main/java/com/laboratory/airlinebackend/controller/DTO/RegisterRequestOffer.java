package com.laboratory.airlinebackend.controller.DTO;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestOffer {

    private double discount;
    @Column(length = 150)
    private String description;
    @Column(length = 100)
    private String origin;
    @Column(length = 100)
    private String destination;
    private Date validDateRange;
}
