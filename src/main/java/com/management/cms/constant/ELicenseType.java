package com.management.cms.constant;

import lombok.Getter;

@Getter
public enum ELicenseType {
    FOOD_SAFETY_CERTIFICATE("FOOD_SAFETY_CERTIFICATE","giấy chứng nhận an toàn thực phẩm"),
    BUSINESS_LICENSE("BUSINESS_LICENSE","giấy phép kinh doanh");


    private String code;
    private String name;
    ELicenseType(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
