package com.management.cms.model.request;

import lombok.Data;

@Data
public class PersonSaveRequest {
    private Long id;
        private String email;
    private String firstName;
    private String lastName;
        private String phoneNumber;
    private String dob;
        private String socialSecurityNum;
    private String positionCode;
    private String image;

    public void validateInput() throws Exception{
        if(this.email == null){
            throw new Exception("Không được bỏ trống email của chủ/ quản lý cơ sở");
        }
        if(this.phoneNumber == null){
            throw new Exception("Không được bỏ trống sđt của chủ/ quản lý cơ sở");
        }
        if(this.socialSecurityNum == null){
            throw new Exception("Không được bỏ trống số căn cước của chủ/ quản lý cơ sở");
        }
    }
}
