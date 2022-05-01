package com.management.cms.service;

import com.management.cms.model.enitity.AreaDoc;
import com.management.cms.model.request.AreaSaveRequest;
import com.management.cms.model.request.AreaSearchRequest;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AreaService {
    AreaDoc createNewArea(AreaSaveRequest areaSaveRequest) throws Exception;
    AreaDoc editArea(AreaSaveRequest areaSaveRequest,Long id) throws Exception;
    PagedListHolder<AreaDoc> searchAllArea(AreaSearchRequest areaSearchRequest, Integer page, Integer size, String sortby);
    AreaDoc getAreaDetailById(Long id) throws Exception;
    AreaDoc lockAndUnlockById(Long id) throws Exception;
}
