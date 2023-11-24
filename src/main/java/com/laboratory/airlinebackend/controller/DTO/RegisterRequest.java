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
public class RegisterRequest {

    private String DNI;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Date birthday;
    private String birthPlace;
    private String billingAddress;
    private String gender;
    private String username;
    private String profileImage;
    //private Boolean subscribedToFeed;

}
