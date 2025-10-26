package com.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class UserServiceErrorResponse {
    private String message;

    public UserServiceErrorResponse(String message) {
        this.message = message;
    }
}
