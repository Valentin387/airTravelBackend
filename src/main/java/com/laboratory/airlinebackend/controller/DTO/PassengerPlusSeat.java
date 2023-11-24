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
public class PassengerPlusSeat {

    @Column(length = 15)
    private String DNI;
    @Column(length = 15)
    private String firstName;
    @Column(length = 15)
    private String lastName;
    private Date birthday;
    @Column(length = 10)
    private String gender;
    @Column(length = 11)
    private String cellphoneNo;
    @Column(length = 50)
    private String email;
    @Column(length = 100)
    private String contactName;
    @Column(length = 100)
    private String contactNo;
    private long seatID;

}
