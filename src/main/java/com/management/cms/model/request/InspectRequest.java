package com.management.cms.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class InspectRequest {
    @NotBlank(message = "Không được bỏ trống ngày thanh tra")
    private String inspectDate;
    private String warningContent;
    private Integer warningStatus;
}
