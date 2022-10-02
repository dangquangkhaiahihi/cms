package com.management.cms.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BusinessTypeSaveRequest {
    private Long id;
    private String code;
    private String name;
}
