package com.academic.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
public class StudentRegisterRequest {

    @NotBlank(message = "学号不能为空")
    @Pattern(regexp = "\\d{10}Z?", message = "学号格式错误：本科学生为10位数字，专升本学生为10位数字加Z")
    private String studentId;

    @NotBlank(message = "姓名不能为空")
    @Size(max = 50, message = "姓名长度不能超过50个字符")
    private String name;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "1[3-9]\\d{9}", message = "手机号格式错误")
    private String phone;

    @NotBlank(message = "邮箱不能为空")
    @Pattern(regexp = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}", message = "邮箱格式错误")
    private String email;

}
