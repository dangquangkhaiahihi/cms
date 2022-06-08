package com.management.cms.controller;

import com.management.cms.constant.Commons;
import com.management.cms.model.dto.BusinessPremisesDto;
import com.management.cms.model.dto.BusinessPremisesSearchDto;
import com.management.cms.model.dto.SearchDtos;
import com.management.cms.model.enitity.BusinessTypeDoc;
import com.management.cms.model.request.BusinessPremisesSaveRequest;
import com.management.cms.model.request.InspectRequest;
import com.management.cms.model.request.ListLicenseRequest;
import com.management.cms.model.request.ListPersonRequest;
import com.management.cms.model.response.BaseResponse;
import com.management.cms.service.BusinessPremisesService;
import com.management.cms.service.BusinessTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/business_premises")
public class BusinessPremisesController {
    @Autowired
    BusinessPremisesService businessPremisesService;

    @Autowired
    BusinessTypeService businessTypeService;

    @GetMapping()
    public ResponseEntity<?> search(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                                    @RequestParam(value = "area", required = false, defaultValue = "") String area,
                                    @RequestParam(value = "businessType", required = false, defaultValue = "") String businessType,
                                    @RequestParam(value = "foodSafetyCertificateProvidedBy", required = false, defaultValue = "") String foodSafetyCertificateProvidedBy,
                                    @RequestParam(value = "licenseStatus", required = false, defaultValue = "2") Integer licenseStatus,
                                    @RequestParam(value = "certificateStatus", required = false, defaultValue = "2") Integer certificateStatus,
                                    @RequestParam(value = "warningStatus", required = false, defaultValue = "2") Integer warningStatus,
                                    // status = 0 -> false, status = 1 -> true, status = 2 -> all
                                    @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                    @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        String sortby = "createAt";
        PagedListHolder<BusinessPremisesSearchDto> businessPremisesSearchDtos = businessPremisesService.search(page, size, sortby, keyword, area, businessType, foodSafetyCertificateProvidedBy, licenseStatus, certificateStatus, warningStatus);

        SearchDtos searchDtos = new SearchDtos();
        searchDtos.setContent(businessPremisesSearchDtos.getPageList());
        searchDtos.setTotalElements(businessPremisesSearchDtos.getNrOfElements());
        searchDtos.setTotalPages(businessPremisesSearchDtos.getPageCount());

        BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_SUCCESS_00);
        baseResponse.setData(searchDtos);
        return ResponseEntity.ok(baseResponse);
    }

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

    @PostMapping("/{id}")
    public ResponseEntity<?> edit(@Validated @RequestBody BusinessPremisesSaveRequest businessPremisesSaveRequest,
                                  @PathVariable Long id) {
        if(!id.equals(businessPremisesSaveRequest.getId())){
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_ERROR_99);
            baseResponse.setDesc("Id không hợp lệ");
            return ResponseEntity.badRequest().body(baseResponse);
        }
        try {
            String result = businessPremisesService.editBusinessPremises(businessPremisesSaveRequest);
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_SUCCESS_00);
            baseResponse.setData(result);
            return ResponseEntity.ok(baseResponse);
        } catch (Exception e) {
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_ERROR_99);
            baseResponse.setDesc(e.getMessage());
            return ResponseEntity.badRequest().body(baseResponse);
        }
    }

    @PostMapping("/add_licenses/{premisesId}")
    public ResponseEntity<?> saveLicenseToPremises(@RequestBody ListLicenseRequest listLicenseRequest,
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

    @PostMapping("/add_people/{premisesId}")
    public ResponseEntity<?> savePeopleToPremises(@RequestBody ListPersonRequest listPersonRequest,
                                                   @PathVariable Long premisesId) {
        try {
            String result = businessPremisesService.addPersonToPremises(listPersonRequest,premisesId);
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_SUCCESS_00);
            baseResponse.setData(result);
            return ResponseEntity.ok(baseResponse);
        } catch (Exception e) {
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_ERROR_99);
            baseResponse.setDesc(e.getMessage());
            return ResponseEntity.badRequest().body(baseResponse);
        }
    }

    @PutMapping("/update_inspect/{premisesId}")
    public ResponseEntity<?> updateInspect(@Validated @RequestBody InspectRequest inspectRequest,
                                                   @PathVariable Long premisesId) {
        try {
            String result = businessPremisesService.updateInspect(inspectRequest,premisesId);
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_SUCCESS_00);
            baseResponse.setData(result);
            return ResponseEntity.ok(baseResponse);
        } catch (Exception e) {
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_ERROR_99);
            baseResponse.setDesc(e.getMessage());
            return ResponseEntity.badRequest().body(baseResponse);
        }
    }

    @GetMapping("/{premisesId}")
    public ResponseEntity<?> getDetailById(@PathVariable Long premisesId) {
        try {
            BusinessPremisesDto businessPremisesDto = businessPremisesService.getDeailBusinessPremises(premisesId);
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_SUCCESS_00);
            baseResponse.setData(businessPremisesDto);
            return ResponseEntity.ok(baseResponse);
        } catch (Exception e) {
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_ERROR_99);
            baseResponse.setDesc(e.getMessage());
            return ResponseEntity.badRequest().body(baseResponse);
        }
    }

    @GetMapping(value = "/getAllActiveBusinessType")
    public ResponseEntity<?> getAllActiveBusinessType(){
        List<BusinessTypeDoc> businessTypeDocs = businessTypeService.getAllActiveBusinessType();
        BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_SUCCESS_00);
        baseResponse.setData(businessTypeDocs);
        return ResponseEntity.ok(baseResponse);
    }
}
