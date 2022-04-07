package com.management.cms.constant;

import lombok.Getter;

@Getter
public enum EPermission {
    //ADMIN
    BUSINESS_PREMISES_VIEW("BUSINESS_PREMISES_VIEW", "Xem thông tin"),
    BUSINESS_PREMISES_CREATE("BUSINESS_PREMISES_CREATE", "Thêm mới"),
    BUSINESS_PREMISES_EDIT("BUSINESS_PREMISES_EDIT", "Sửa thông tin"),
    ;
    //USER

    private String code;
    private String name;
    EPermission(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
