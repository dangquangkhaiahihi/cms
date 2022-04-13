package com.management.cms.model.request;

import lombok.Data;

@Data
public class LicenseSaveRequest {
    private Long id;
    private String regno;
        private String createdDate;
        private String expirationDate;
        private String providerCode;
        private String licenseTypeCode;

    private String image;

    public void validateInput() throws Exception{
        if(this.regno == null){
            throw new Exception("Không được bỏ trống mã giấy chứng nhận");
        }
        if(this.createdDate == null){
            throw new Exception("Không được bỏ trống ngày hiệu lực");
        }
        if(this.expirationDate == null){
            throw new Exception("Không được bỏ trống ngày hết hạn");
        }
    }
}
