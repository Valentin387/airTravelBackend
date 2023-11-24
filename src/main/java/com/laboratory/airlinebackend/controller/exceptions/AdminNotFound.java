package com.laboratory.airlinebackend.controller.exceptions;

public class AdminNotFound extends RuntimeException {
    public AdminNotFound(String message) {
        super(message);
    }
}
