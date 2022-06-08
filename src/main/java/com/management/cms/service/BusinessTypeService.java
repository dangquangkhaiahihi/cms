package com.management.cms.service;

import com.management.cms.model.enitity.BusinessTypeDoc;
import com.management.cms.model.request.BusinessTypeSaveRequest;
import com.management.cms.model.request.BusinessTypeSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BusinessTypeService {
    BusinessTypeDoc createNewType(BusinessTypeSaveRequest businessTypeSaveRequest) throws Exception;
    BusinessTypeDoc editType(BusinessTypeSaveRequest businessTypeSaveRequest,Long id) throws Exception;
    Page<BusinessTypeDoc> searchAllType(BusinessTypeSearchRequest businessTypeSearchRequest, Pageable pageable);
    BusinessTypeDoc getTypeDetailById(Long id) throws Exception;
    BusinessTypeDoc lockAndUnlockById(Long id) throws Exception;

    List<BusinessTypeDoc> getAllActiveBusinessType();
}
