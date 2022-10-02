package com.management.cms.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ChangePassRequest {
    private String passwordOld;
    private String passwordNew;
    private String rePassword;
}
