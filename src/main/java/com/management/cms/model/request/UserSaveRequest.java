package com.management.cms.model.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

@Data
public class UserSaveRequest {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String dob;
    private String socialSecurityNum;
    private String role;
    private String areaCode;
    private MultipartFile photo;

    public void validateInput() throws Exception{

    }
}
