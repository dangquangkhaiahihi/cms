package com.management.cms.model.response;

import lombok.Data;

@Data
public class AccountLoginResponse {
    private Long userId;
    private String username;
    private String fullName;
    private String email;

    public AccountLoginResponse(Long id, String username, String email, String fullName) {
        this.userId = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
    }

}
