package com.management.cms.model.request;

import com.management.cms.constant.ELicenseType;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class ListLicenseRequest {
    private List<LicenseSaveRequest> licenses;

    public void validateInput() throws Exception{
        if(this.licenses.size() > 2){
            throw new Exception("Một cơ sở chỉ được có 1 giấy ĐKKD và 1 giấy chứng nhận ATTP");
        }

        //check không được có 2 ĐKKD hoặc 2 chứng nhận ATTP
        List<String> businessLicense = new ArrayList<>();
        List<String> certificate = new ArrayList<>();

        //check mã đăng ký ko đc trùng giữa 2 đứa đối tượng save req
        Set<String> regnos = new HashSet<>();
        for(LicenseSaveRequest licenseSaveRequest : this.licenses){
            regnos.add(licenseSaveRequest.getRegno());
            if(licenseSaveRequest.getLicenseTypeCode().equals(ELicenseType.BUSINESS_LICENSE.getCode())){
                businessLicense.add(licenseSaveRequest.getLicenseTypeCode());
            }
            if(licenseSaveRequest.getLicenseTypeCode().equals(ELicenseType.FOOD_SAFETY_CERTIFICATE.getCode())){
                certificate.add(licenseSaveRequest.getLicenseTypeCode());
            }
        }
        if(regnos.size() != this.licenses.size()){
            throw new Exception("Mã số đăng ký bị trùng");
        }
        if(businessLicense.size() > 1 || certificate.size() > 1){
            throw new Exception("Một cơ sở chỉ được có 1 giấy ĐKKD và 1 giấy chứng nhận ATTP");
        }
    }

}
