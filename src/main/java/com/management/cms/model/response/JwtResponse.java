package com.management.cms.model.response;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class JwtResponse {
    private String token;
    private AccountLoginResponse account;
    private Set<String> roles;
    private String type = "Bearer";

    public JwtResponse(String accessToken, Long id, String username, String email, String fullName, Set<String> roles, List<String> areas) {
        AccountLoginResponse accout = new AccountLoginResponse(id,username,email,fullName,areas);
        this.token = accessToken;
        this.account = accout;
        this.roles = roles;
    }

}
