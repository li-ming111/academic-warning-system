package com.academic.dto;

import lombok.Data;

@Data
public class StudentRegisterRequest {

    private String studentId;

    private String name;

    private String password;

    private String phone;

    private String email;

}
