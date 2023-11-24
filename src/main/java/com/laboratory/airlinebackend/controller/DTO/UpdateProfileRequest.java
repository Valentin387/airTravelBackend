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
public class UpdateProfileRequest {
    private String email;
    private String DNI;
    private String firstName;
    private String lastName;
    private Date birthday;
    private String birthPlace;
    private String billingAddress;
    private String gender;
    private int role;
    private String username;
    private String profileImage;
    private Boolean active;
    private Boolean subscribedToFeed;
}
