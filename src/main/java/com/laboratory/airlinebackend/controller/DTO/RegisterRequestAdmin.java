package com.laboratory.airlinebackend.controller.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestAdmin {

    private String firstName;
    private String email;
    private String password;

    // Constructor that generates a random password
    public RegisterRequestAdmin(String firstName, String email) {
        this.firstName = firstName;
        this.email = email;
        this.password = ""; /*this.generateRandomPassword();*/

    }

        // Method that generates a random password
        public void generateRandomPassword() {
            int passwordLength = 8; // Random password length
            this.password = RandomStringUtils.randomAlphanumeric(passwordLength);
            System.out.println("Password: " + password); // Print random password to console
        }

    }

