package com.laboratory.airlinebackend.controller.DTO;

import lombok.*;

@Data
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest {

    private String email;
    String password;
}
