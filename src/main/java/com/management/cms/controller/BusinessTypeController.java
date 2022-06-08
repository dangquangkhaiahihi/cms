package com.management.cms.controller;

import com.management.cms.constant.Commons;
import com.management.cms.model.enitity.BusinessTypeDoc;
import com.management.cms.model.request.BusinessTypeSaveRequest;
import com.management.cms.model.request.BusinessTypeSearchRequest;
import com.management.cms.model.response.BaseResponse;
import com.management.cms.service.BusinessTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/business_type")
public class BusinessTypeController {
    @Autowired
    BusinessTypeService businessTypeService;

    @GetMapping()
    public ResponseEntity<?> search(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                                    @RequestParam(value = "status", required = false, defaultValue = "2") Integer status,
                                    // status = 0 -> false, status = 1 -> true, status = 2 -> all
                                    @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                    @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
    ) {
        BusinessTypeSearchRequest businessTypeSearchRequest = new BusinessTypeSearchRequest();
        businessTypeSearchRequest.setKeyword(keyword);
        businessTypeSearchRequest.setStatus(status);

        Sort sort = null;
        sort = Sort.by("code").descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<BusinessTypeDoc> businessTypeDocs = businessTypeService.searchAllType(businessTypeSearchRequest, pageable);
        BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_SUCCESS_00);
        baseResponse.setData(businessTypeDocs);
        return ResponseEntity.ok(baseResponse);
    }


    @PostMapping()
    public ResponseEntity<?> save(@Validated @RequestBody BusinessTypeSaveRequest businessTypeSaveRequest) {
        try {
            BusinessTypeDoc businessTypeDoc = businessTypeService.createNewType(businessTypeSaveRequest);
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_SUCCESS_00);
            baseResponse.setData(businessTypeDoc);
            return ResponseEntity.ok(baseResponse);
        } catch (Exception e) {
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_ERROR_99);
            baseResponse.setDesc(e.getMessage());
            return ResponseEntity.badRequest().body(baseResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody BusinessTypeSaveRequest businessTypeSaveRequest,
                                    @PathVariable Long id) {

        try {
            BusinessTypeDoc businessTypeDoc = businessTypeService.editType(businessTypeSaveRequest,id);
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_SUCCESS_00);
            baseResponse.setData(businessTypeDoc);
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
            BusinessTypeDoc businessTypeDoc = businessTypeService.getTypeDetailById(id);
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_SUCCESS_00);
            baseResponse.setData(businessTypeDoc);
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
            BusinessTypeDoc businessTypeDoc = businessTypeService.lockAndUnlockById(id);
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_SUCCESS_00);
            baseResponse.setData(businessTypeDoc);
            return ResponseEntity.ok(baseResponse);
        } catch (Exception e) {
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_ERROR_99);
            baseResponse.setDesc(e.getMessage());
            return ResponseEntity.badRequest().body(baseResponse);
        }
    }
}
