package com.management.cms.model.request;

import lombok.Data;

@Data
public class BusinessPremisesSaveRequest {
    private Long id;
    private String name;
    private String addressDetail;
    private String addressGeneral;
    private String image;
    private String businessTypeCode;
    private String areaCode;
}
