package com.management.cms.model.request;

import lombok.Data;

@Data
public class AreaSearchRequest {
    String name;
    String code;
    Integer status;
}
