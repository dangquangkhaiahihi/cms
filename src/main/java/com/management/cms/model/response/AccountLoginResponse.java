package com.management.cms.model.response;

import lombok.Data;

import java.util.List;

@Data
public class AccountLoginResponse {
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private List<String> area;

    public AccountLoginResponse(Long id, String username, String email, String fullName, List<String> area) {
        this.userId = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.area = area;
    }

}
