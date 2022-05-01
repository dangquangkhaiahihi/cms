package com.management.cms.service;

import com.management.cms.model.dto.BusinessPremisesDto;
import com.management.cms.model.dto.BusinessPremisesSearchDto;
import com.management.cms.model.request.BusinessPremisesSaveRequest;
import com.management.cms.model.request.InspectRequest;
import com.management.cms.model.request.ListLicenseRequest;
import com.management.cms.model.request.ListPersonRequest;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BusinessPremisesService {
    String createNewBusinessPremises(BusinessPremisesSaveRequest businessPremisesSaveRequest) throws Exception;

    String addLicensesToPremises(ListLicenseRequest listLicenseRequest, Long premisesId) throws Exception;

    String addPersonToPremises(ListPersonRequest listPersonRequest, Long premisesId) throws Exception;

    String updateInspect(InspectRequest inspectRequest, Long premisesId) throws Exception;

    BusinessPremisesDto getDeailBusinessPremises(Long id) throws Exception;

    PagedListHolder<BusinessPremisesSearchDto> search(Integer page, Integer size, String sortby, String keyword, String area, String businessType, String foodSafetyCertificateProvidedBy, Integer licenseStatus, Integer certificateStatus, Integer warningStatus);
}
