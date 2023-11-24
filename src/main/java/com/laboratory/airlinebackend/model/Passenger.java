package com.laboratory.airlinebackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "tblPassenger")
public class Passenger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long ID;
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
    private boolean didCheckIn;
}
