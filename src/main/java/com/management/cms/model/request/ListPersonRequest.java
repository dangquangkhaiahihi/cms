package com.management.cms.model.request;

import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class ListPersonRequest {
    List<PersonSaveRequest> personSaveRequests;

    public void validateInput() throws Exception{
        Set<String> emails = new HashSet<>();
        Set<String> phones = new HashSet<>();
        Set<String> ssns = new HashSet<>();

        for(PersonSaveRequest personSaveRequest : this.personSaveRequests){
            emails.add(personSaveRequest.getEmail());
            phones.add(personSaveRequest.getPhoneNumber());
            ssns.add(personSaveRequest.getSocialSecurityNum());
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
    }
}
