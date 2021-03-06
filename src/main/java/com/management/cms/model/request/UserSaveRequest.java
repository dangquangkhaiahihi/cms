package com.management.cms.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserSaveRequest {
    private Long id;
    @NotBlank(message = "Email không được trống")
    private String email;
    @NotBlank(message = "Họ không được trống")
    private String firstName;
    @NotBlank(message = "Tên không được trống")
    private String lastName;
    @NotBlank(message = "Số điện thoại không được trống")
    private String phoneNumber;
    @NotBlank(message = "Ngày sinh không được trống")
    private String dob;
    @NotBlank(message = "Số căn cước công dân không được trống")
    private String socialSecurityNum;

    private String role;

    private String areaCode;

    public void validateInput() throws Exception{

    }
}
