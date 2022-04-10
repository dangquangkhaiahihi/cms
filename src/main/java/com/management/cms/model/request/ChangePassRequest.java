package com.management.cms.model.request;

import lombok.Data;

@Data
public class ChangePassRequest {
    private String passwordOld;
    private String passwordNew;
    private String rePassword;
}
