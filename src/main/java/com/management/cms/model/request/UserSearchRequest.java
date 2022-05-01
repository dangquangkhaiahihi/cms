package com.management.cms.model.request;

import lombok.Data;

@Data
public class UserSearchRequest {
    String keyword;
    String area;
    Integer enabled;
    String role;
}
