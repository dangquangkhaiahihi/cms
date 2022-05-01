package com.management.cms.model.dto;

import lombok.Data;

@Data
public class BusinessPremisesSearchDto {
    private Long id;
    private String name;//crud
    private String addressDetail;//crud
    private String addressGeneral;//crud
            private String businessType;//crud
            private String area;//crud
    private String ownerInfo; //crud
    private String managerInfo;//crud

        private String foodSafetyCertificateProvidedBy;//crud
        private Integer licenseStatus;//crud
        private Integer certificateStatus;//crud
        private Integer warningStatus;//crud
}
