package com.management.cms.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class InspectRequest {
    private String inspectDate;
    private String warningContent;
    private Integer warningStatus;
}
