package com.academic.entity;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String role;
    private String email;
    private String phone;
    private String status;
    private String createdAt;
    private String updatedAt;
}
