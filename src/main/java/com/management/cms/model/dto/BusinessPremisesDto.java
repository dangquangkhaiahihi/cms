package com.management.cms.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class BusinessPremisesDto {
    private Long id;
    private String name;//crud
    private String addressDetail;//crud
    private String addressGeneral;//crud
    private String image;//detail
    private String businessType;//crud
    private String area;//crud
    private List<PersonDto> people = new ArrayList<>();// not in CRUD table
    private String ownerInfo; //crud
    private String managerInfo;//crud

    private List<LicenseDto> licenses = new ArrayList<>(); // not in CRUD table
    private String foodSafetyCertificateProvidedBy;//crud
    private String foodSafetyCertificateStartDate;//detail
    private String foodSafetyCertificateEndDate;//detail
    private Integer licenseStatus;//crud
    private Integer certificateStatus;//crud
    private String businessLicenseRegno;//detail

    private String lastInspectDate;//detail
    private String inspectDate;//detail
    private Integer warningStatus;//crud
    private String warningContent;//detail
}