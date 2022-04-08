package com.management.cms.service;

import com.management.cms.model.enitity.AreaDoc;
import com.management.cms.model.request.AreaSaveRequest;
import com.management.cms.model.request.AreaSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AreaService {
    AreaDoc createNewArea(AreaSaveRequest areaSaveRequest) throws Exception;
    AreaDoc editArea(AreaSaveRequest areaSaveRequest,Long id) throws Exception;
    Page<AreaDoc> searchAllArea(AreaSearchRequest areaSearchRequest, Pageable pageable);
    AreaDoc getAreaDetailById(Long id) throws Exception;
    AreaDoc lockAndUnlockById(Long id) throws Exception;
}
