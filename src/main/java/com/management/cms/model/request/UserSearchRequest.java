package com.management.cms.model.request;

import lombok.Data;

@Data
public class UserSearchRequest {
    String email;
    String phoneNumber;
    String socialSecurityNum;
    String area;
    Integer enabled;
    String role;
}
