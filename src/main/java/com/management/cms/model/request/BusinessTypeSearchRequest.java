package com.management.cms.model.request;

import lombok.Data;

@Data
public class BusinessTypeSearchRequest {
    private String keyword;
    private Integer status;
}
