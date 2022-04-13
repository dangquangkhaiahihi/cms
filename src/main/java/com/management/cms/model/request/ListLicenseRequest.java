package com.management.cms.model.request;

import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class ListLicenseRequest {
    private List<LicenseSaveRequest> licenses;

    public void validateInput() throws Exception{
        Set<String> regnos = new HashSet<>();
        for(LicenseSaveRequest licenseSaveRequest : this.licenses){
            regnos.add(licenseSaveRequest.getRegno());
        }
        if(regnos.size() != this.licenses.size()){
            throw new Exception("Mã số giấy chứng nhận bị trùng");
        }
    }

}
