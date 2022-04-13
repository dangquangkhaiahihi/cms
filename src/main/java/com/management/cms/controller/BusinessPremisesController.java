package com.management.cms.controller;

import com.management.cms.constant.Commons;
import com.management.cms.model.request.BusinessPremisesSaveRequest;
import com.management.cms.model.request.ListLicenseRequest;
import com.management.cms.model.response.BaseResponse;
import com.management.cms.service.BusinessPremisesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/business_premises")
public class BusinessPremisesController {
    @Autowired
    BusinessPremisesService businessPremisesService;

    @PostMapping()
    public ResponseEntity<?> save(@Validated @RequestBody BusinessPremisesSaveRequest businessPremisesSaveRequest) {
        try {
            String result = businessPremisesService.createNewBusinessPremises(businessPremisesSaveRequest);
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_SUCCESS_00);
            baseResponse.setData(result);
            return ResponseEntity.ok(baseResponse);
        } catch (Exception e) {
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_ERROR_99);
            baseResponse.setDesc(e.getMessage());
            return ResponseEntity.badRequest().body(baseResponse);
        }
    }

    @PostMapping("/add_license/{premisesId}")
    public ResponseEntity<?> saveLicenseToPremises(@Validated @RequestBody ListLicenseRequest listLicenseRequest,
                                                  @PathVariable Long premisesId) {
        try {
            String result = businessPremisesService.addLicensesToPremises(listLicenseRequest,premisesId);
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_SUCCESS_00);
            baseResponse.setData(result);
            return ResponseEntity.ok(baseResponse);
        } catch (Exception e) {
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_ERROR_99);
            baseResponse.setDesc(e.getMessage());
            return ResponseEntity.badRequest().body(baseResponse);
        }
    }
}
