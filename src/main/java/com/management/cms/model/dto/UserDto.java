package com.management.cms.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String phoneNumber;
    private String dob;
    private String socialSecurityNum;
    private Integer status;
    private List<String> areas = new ArrayList<>();
    private String role;
    private LocalDateTime createdAt;
    private String areaCode;
}
