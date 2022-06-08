package com.management.cms.controller;

import com.management.cms.constant.Commons;
import com.management.cms.model.dto.SearchDtos;
import com.management.cms.model.enitity.AreaDoc;
import com.management.cms.model.enitity.UserDoc;
import com.management.cms.model.request.AreaSaveRequest;
import com.management.cms.model.request.AreaSearchRequest;
import com.management.cms.model.response.BaseResponse;
import com.management.cms.service.AreaService;
import com.management.cms.utils.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/area")
public class AreaController {
    @Autowired
    AreaService areaService;

    @GetMapping()
    public ResponseEntity<?> search(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                                    @RequestParam(value = "status", required = false, defaultValue = "2") Integer status,
                                    // status = 0 -> false, status = 1 -> true, status = 2 -> all
                                    @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                    @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
    ) {
        AreaSearchRequest areaSearchRequest = new AreaSearchRequest();
        areaSearchRequest.setKeyword(keyword);
        areaSearchRequest.setStatus(status);

        String sortby = "code";
        PagedListHolder<AreaDoc> areas = areaService.searchAllArea(areaSearchRequest, page, size, sortby);

        SearchDtos searchDtos = new SearchDtos();
        searchDtos.setContent(areas.getPageList());
        searchDtos.setTotalElements(areas.getNrOfElements());
        searchDtos.setTotalPages(areas.getPageCount());

        BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_SUCCESS_00);
        baseResponse.setData(searchDtos);
//        baseResponse.setData(areas);
        return ResponseEntity.ok(baseResponse);
    }


    @PostMapping()
    public ResponseEntity<?> save(@Validated @RequestBody AreaSaveRequest areaSaveRequest) {
        try {
            AreaDoc areaDoc = areaService.createNewArea(areaSaveRequest);
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_SUCCESS_00);
            baseResponse.setData(areaDoc);
            return ResponseEntity.ok(baseResponse);
        } catch (Exception e) {
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_ERROR_99);
            baseResponse.setDesc(e.getMessage());
            return ResponseEntity.badRequest().body(baseResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody AreaSaveRequest areaSaveRequest,
                                    @PathVariable Long id) {

        try {
            AreaDoc areaDoc = areaService.editArea(areaSaveRequest,id);
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_SUCCESS_00);
            baseResponse.setData(areaDoc);
            return ResponseEntity.ok(baseResponse);
        } catch (Exception e) {
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_ERROR_99);
            baseResponse.setDesc(e.getMessage());
            return ResponseEntity.badRequest().body(baseResponse);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDetail(@PathVariable Long id) {
        try {
            AreaDoc areaDoc = areaService.getAreaDetailById(id);
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_SUCCESS_00);
            baseResponse.setData(areaDoc);
            return ResponseEntity.ok(baseResponse);
        } catch (Exception e) {
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_ERROR_99);
            baseResponse.setDesc(e.getMessage());
            return ResponseEntity.badRequest().body(baseResponse);
        }
    }

    @PutMapping("/lockAndUnlock/{id}")
    public ResponseEntity<?> lockAndUnlock(@PathVariable Long id) {
        try {
            AreaDoc areaDoc = areaService.lockAndUnlockById(id);
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_SUCCESS_00);
            baseResponse.setData(areaDoc);
            return ResponseEntity.ok(baseResponse);
        } catch (Exception e) {
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_ERROR_99);
            baseResponse.setDesc(e.getMessage());
            return ResponseEntity.badRequest().body(baseResponse);
        }
    }

    @GetMapping("/try_current_user")
    public ResponseEntity<?> tryFindCurrentUser() {
        try {
            UserDoc currentUser = WebUtils.getCurrentUser().getUser();
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_SUCCESS_00);
            baseResponse.setData(currentUser);
            return ResponseEntity.ok(baseResponse);
        } catch (Exception e) {
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_ERROR_99);
            baseResponse.setDesc(e.getMessage());
            return ResponseEntity.badRequest().body(baseResponse);
        }
    }
}
