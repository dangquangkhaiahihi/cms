package com.management.cms.model.dto;

import lombok.Data;

@Data
public class LicenseDto {
    private Long id;
    private String regno;
    private String createdDate;
    private String expirationDate;
    private String provider;
    private String licenseType;

    private String image;
}
