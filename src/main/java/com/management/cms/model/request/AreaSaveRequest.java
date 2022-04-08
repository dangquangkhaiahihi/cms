package com.management.cms.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AreaSaveRequest {
    Long id;
    @NotBlank(message = "Tên khu vực không được trống")
    String name;
    @NotBlank(message = "Mã khu vực không được trống")
    String code;
}
