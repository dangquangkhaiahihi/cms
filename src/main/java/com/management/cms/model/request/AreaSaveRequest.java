package com.management.cms.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AreaSaveRequest {
    Long id;
    String name;
    String code;

    public void vailidateInput() throws Exception{
        if(this.name.isEmpty()){
            throw new Exception("Tên khu vực không được trống");
        }
        if(this.code.isEmpty()){
            throw new Exception("Mã khu vực không được trống");
        }
    }
}
