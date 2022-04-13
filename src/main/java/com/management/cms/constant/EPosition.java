package com.management.cms.constant;

import lombok.Getter;

@Getter
public enum EPosition {
    OWNER("OWNER","Chủ sở hữu"),
    MANAGER("MANAGER","Quản lý");

    private String code;
    private String name;
    EPosition(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
