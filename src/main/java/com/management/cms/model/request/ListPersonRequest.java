package com.management.cms.model.request;

import com.management.cms.constant.ELicenseType;
import com.management.cms.constant.EPosition;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class ListPersonRequest {
    List<PersonSaveRequest> personSaveRequests;

    public void validateInput() throws Exception{
        if(this.personSaveRequests.size() > 2){
            throw new Exception("Một cơ sở chỉ được có 1 chủ sở hữu và 1 đại diện quản lý");
        }

        //check không được có 2 chủ hoặc 2 quản lý
        List<String> owner = new ArrayList<>();
        List<String> manager = new ArrayList<>();

        Set<String> emails = new HashSet<>();
        Set<String> phones = new HashSet<>();
        Set<String> ssns = new HashSet<>();

        for(PersonSaveRequest personSaveRequest : this.personSaveRequests){
            emails.add(personSaveRequest.getEmail());
            phones.add(personSaveRequest.getPhoneNumber());
            ssns.add(personSaveRequest.getSocialSecurityNum());
            if(personSaveRequest.getPositionCode().equals(EPosition.OWNER.getCode())){
                owner.add(personSaveRequest.getPositionCode());
            }
            if(personSaveRequest.getPositionCode().equals(EPosition.MANAGER.getCode())){
                manager.add(personSaveRequest.getPositionCode());
            }
        }
        if(emails.size() != this.personSaveRequests.size()){
            throw new Exception("Email bị trùng");
        }
        if(phones.size() != this.personSaveRequests.size()){
            throw new Exception("Số điện thoại bị trùng");
        }
        if(ssns.size() != this.personSaveRequests.size()){
            throw new Exception("Số căn cước bị trùng");
        }
        if(owner.size() > 1 || manager.size() > 1){
            throw new Exception("Một cơ sở chỉ được có 1 chủ sở hữu và 1 đại diện quản lý");
        }
    }
}
