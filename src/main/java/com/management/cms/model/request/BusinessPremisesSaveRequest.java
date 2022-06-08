package com.management.cms.model.request;

import lombok.Data;

import java.util.Objects;

@Data
public class BusinessPremisesSaveRequest {
    private Long id;
    private String name;
    private String addressDetail;
    private String addressGeneral;
    private String image;
    private String businessTypeCode;
    private String areaCode;

    public void validateInput() throws Exception{
        if(this.name == null || this.name == ""){
            throw new Exception("Không được bỏ trống tên cơ sở");
        }
        if(this.addressDetail == null || this.addressDetail == ""){
            throw new Exception("Không được bỏ trống địa chỉ chi tiết");
        }
        if(this.businessTypeCode == null || this.businessTypeCode == "") {
            throw new Exception("Không được bỏ trống loại hình kinh doanh");
        }
        if(this.areaCode == null || this.areaCode == ""){
            throw new Exception("Không được bỏ trống khu vực");
        }
    }
}
