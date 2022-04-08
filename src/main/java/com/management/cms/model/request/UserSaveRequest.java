package com.management.cms.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    @NotBlank(message = "Khu vực phụ trách không được trống")
    private String areaCode;
    @NotBlank(message = "Ngày sinh không được trống")
    private LocalDateTime dob;
    @NotBlank(message = "Số căn cước công dân không được trống")
    private String socialSecurityNum;

    private List<String> areaCodes = new ArrayList<>();

    public void validateInput(){

    }
}
