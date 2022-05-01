package com.management.cms.model.dto;

import lombok.Data;

@Data
public class PersonDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String dob;
    private String socialSecurityNum;
    private String position;

    private String image;
}
