package com.mytech.virtualcourse.exceptions;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorResponse {

    // Getter and setter methods
    private int status;
    private String message;

    // No-argument constructor is required for Jackson
    public ErrorResponse() {}

    // Constructor to initialize the fields
    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

}
