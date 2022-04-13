package com.management.cms.constant;

import lombok.Getter;

@Getter
public enum EProvider {
    QUAN("QUAN", "quận"),
    THANH_PHO("THANH_PHO", "thành phố");

    private String code;
    private String name;

    EProvider(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
