package com.management.cms.service.implement;

import com.management.cms.constant.Commons;
import com.management.cms.constant.ELicenseType;
import com.management.cms.constant.EProvider;
import com.management.cms.model.enitity.*;
import com.management.cms.model.request.BusinessPremisesSaveRequest;
import com.management.cms.model.request.LicenseSaveRequest;
import com.management.cms.model.request.ListLicenseRequest;
import com.management.cms.repository.AreaRepository;
import com.management.cms.repository.BusinessPremisesRepository;
import com.management.cms.repository.BusinessTypeRepository;
import com.management.cms.repository.LicenseRepository;
import com.management.cms.service.BusinessPremisesService;
import com.management.cms.service.GeneratorSeqService;
import com.management.cms.utils.Utils;
import com.management.cms.utils.WebUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BusinessPremisesServiceImpl implements BusinessPremisesService {
    @Autowired
    private GeneratorSeqService generatorSeqService;

    @Autowired
    BusinessPremisesRepository businessPremisesRepository;

    @Autowired
    AreaRepository areaRepository;

    @Autowired
    BusinessTypeRepository businessTypeRepository;

    @Autowired
    LicenseRepository licenseRepository;

    Utils utils = new Utils();

    public Boolean checkIfUserAndPremisesHaveSameArea(String areaCode){
        UserDoc currentUser = WebUtils.getCurrentUser().getUser();
        Boolean allow = false;
        for(AreaDoc areaDoc : currentUser.getAreas()){
            if(areaDoc.getCode().equals(areaCode)){
                allow = true;
                break;
            }
        }
        return allow;
    }

    @Override
    public String createNewBusinessPremises(BusinessPremisesSaveRequest businessPremisesSaveRequest) throws Exception{
        //check valid area and business type code
        AreaDoc area = areaRepository.findByCode(businessPremisesSaveRequest.getAreaCode());
        if(area == null) throw new Exception("Không có khu vực có mã " + businessPremisesSaveRequest.getAreaCode());

        BusinessTypeDoc businessType = businessTypeRepository.findByCode(businessPremisesSaveRequest.getBusinessTypeCode());
        if (businessType == null ) throw new Exception("Không có loại hình có mã " + businessPremisesSaveRequest.getBusinessTypeCode());

        //current user can only do stuff in his assigned area(s)
        Boolean allow = checkIfUserAndPremisesHaveSameArea(businessPremisesSaveRequest.getAreaCode());
        if(!allow) throw new Exception("Người dùng không thể thực hiện thao tác ở khu vực " + businessPremisesSaveRequest.getAreaCode());

        BusinessPremisesDoc businessPremisesDoc = new BusinessPremisesDoc();
        BeanUtils.copyProperties(businessPremisesSaveRequest,businessPremisesDoc);
        businessPremisesDoc.setBusinessType(businessType);
        businessPremisesDoc.setAreaDoc(area);
        businessPremisesDoc.setWarningStatus(Commons.WARNING_STATUS_OK);

        businessPremisesDoc.setId(generatorSeqService.getNextSequenceId(businessPremisesDoc.SEQUENCE_NAME));
        businessPremisesRepository.save(businessPremisesDoc);
        return "Lưu cơ sở kinh doanh thành công";
    }

    @Override
    public String addLicensesToPremises(ListLicenseRequest listLicenseRequest, Long premisesId) throws Exception {
        if(listLicenseRequest.getLicenses().isEmpty()) throw new Exception("Không thấy giấy chứng nhận để lưu");

        try{
            listLicenseRequest.validateInput();
        }catch (Exception e){
            throw e;
        }

        Optional<BusinessPremisesDoc> optionalPremises = businessPremisesRepository.findById(premisesId);
        if(!optionalPremises.isPresent()) throw new Exception("Id cơ sở không hợp lệ");

        BusinessPremisesDoc businessPremisesDoc = optionalPremises.get();
        checkIfUserAndPremisesHaveSameArea(businessPremisesDoc.getAreaDoc().getCode());

        //lưu license vào DB trước
        List<LicenseDoc> licenseDocList = new ArrayList<>();
        for(LicenseSaveRequest licenseSaveRequest : listLicenseRequest.getLicenses()){
            try{
                licenseSaveRequest.validateInput();
            }catch (Exception e){
                throw e;
            }
            if(licenseRepository.existsByRegno(licenseSaveRequest.getRegno())){
                throw new Exception("Số giấy chứng nhận đã tồn tại");
            }
            LicenseDoc licenseDoc = new LicenseDoc();
            BeanUtils.copyProperties(licenseSaveRequest,licenseDoc);
            LocalDateTime createdDate = utils.convertStringToLocalDateTime01(licenseSaveRequest.getCreatedDate());
            LocalDateTime expirationDate = utils.convertStringToLocalDateTime01(licenseSaveRequest.getExpirationDate());
            licenseDoc.setCreatedDate(createdDate);
            licenseDoc.setExpirationDate(expirationDate);
            if(EProvider.QUAN.getCode().equals(licenseSaveRequest.getProviderCode())){
                licenseDoc.setProvider(EProvider.QUAN);
            }
            if(EProvider.THANH_PHO.getCode().equals(licenseSaveRequest.getProviderCode())){
                licenseDoc.setProvider(EProvider.THANH_PHO);
            }
            if(ELicenseType.FOOD_SAFETY_CERTIFICATE.getCode().equals(licenseSaveRequest.getLicenseTypeCode())){
                licenseDoc.setLicenseType(ELicenseType.FOOD_SAFETY_CERTIFICATE);
            }
            if(ELicenseType.BUSINESS_LICENSE.getCode().equals(licenseSaveRequest.getLicenseTypeCode())){
                licenseDoc.setLicenseType(ELicenseType.BUSINESS_LICENSE);
            }
            licenseDoc.setId(generatorSeqService.getNextSequenceId(licenseDoc.SEQUENCE_NAME));
            licenseDocList.add(licenseDoc);
        }
        licenseRepository.saveAll(licenseDocList);

        //update thông tin license trong business premises
        licenseDocList.stream().forEach(item -> businessPremisesDoc.getLicenses().add(item));
        businessPremisesDoc.updateLicenseInfo(licenseDocList);
        businessPremisesRepository.save(businessPremisesDoc);

        return "Thêm giấy chứng nhận cho cơ sở thành công";
    }
}
