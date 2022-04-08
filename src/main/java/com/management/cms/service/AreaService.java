package com.management.cms.service;

import com.management.cms.constant.ESystemEmail;
import com.management.cms.model.dto.UserDto;
import com.management.cms.model.enitity.AreaDoc;
import com.management.cms.model.request.AreaSaveRequest;
import com.management.cms.model.request.AreaSearchRequest;
import com.management.cms.model.request.UserSaveRequest;
import com.management.cms.model.request.UserSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.Set;

public interface AreaService {
    AreaDoc createNewArea(AreaSaveRequest areaSaveRequest) throws Exception;
    AreaDoc editArea(AreaSaveRequest areaSaveRequest,Long id) throws Exception;
    Page<AreaDoc> searchAllArea(AreaSearchRequest areaSearchRequest, Pageable pageable);
    AreaDoc getAreaDetailById(Long id) throws Exception;
    AreaDoc lockAndUnlockById(Long id) throws Exception;
}
