package com.management.cms.model.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BaseResponse {

    private String mid = "00";
    private String code;
    private String desc;
    private String message;
    private Object data;

    public static BaseResponse parse(String inputMsg) {
        String[] arr = inputMsg.split("\\-", 3);
        if (arr == null || arr.length < 3) {
            return null;
        }
        BaseResponse res = new BaseResponse();
        res.setCode(arr[2]); // it 's normal

        res.setDesc(inputMsg);
        return res;
    }
}
