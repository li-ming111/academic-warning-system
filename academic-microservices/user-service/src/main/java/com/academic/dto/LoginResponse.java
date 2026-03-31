package com.academic.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private Long userId;
    private String username;
    private String name;
    private String role;
    private String email;
    private String phone;
}
