package com.management.cms.service;

import com.management.cms.model.request.BusinessPremisesSaveRequest;
import com.management.cms.model.request.ListLicenseRequest;

public interface BusinessPremisesService {
    String createNewBusinessPremises(BusinessPremisesSaveRequest businessPremisesSaveRequest) throws Exception;

    String addLicensesToPremises(ListLicenseRequest listLicenseRequest, Long premisesId) throws Exception;
}
