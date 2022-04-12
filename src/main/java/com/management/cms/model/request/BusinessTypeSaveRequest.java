package com.management.cms.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BusinessTypeSaveRequest {
    private Long id;
    @NotBlank(message = "Mã loại hình kinh doanh không được trống")
    private String code;
    @NotBlank(message = "Tên loại hình kinh doanh không được trống")
    private String name;
}
