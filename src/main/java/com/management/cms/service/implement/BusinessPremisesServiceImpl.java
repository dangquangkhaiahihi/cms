package com.management.cms.service.implement;

import com.management.cms.constant.Commons;
import com.management.cms.constant.ELicenseType;
import com.management.cms.constant.EPosition;
import com.management.cms.constant.EProvider;
import com.management.cms.model.dto.BusinessPremisesDto;
import com.management.cms.model.dto.BusinessPremisesSearchDto;
import com.management.cms.model.dto.LicenseDto;
import com.management.cms.model.dto.PersonDto;
import com.management.cms.model.enitity.*;
import com.management.cms.model.request.*;
import com.management.cms.repository.*;
import com.management.cms.service.BusinessPremisesService;
import com.management.cms.service.GeneratorSeqService;
import com.management.cms.utils.Utils;
import com.management.cms.utils.WebUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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

    @Autowired
    PersonRepository personRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    Utils utils = new Utils();

    public Boolean checkIfUserAndPremisesHaveSameArea(String areaCode) {
        UserDoc currentUser = WebUtils.getCurrentUser().getUser();
        Boolean allow = false;
        for (AreaDoc areaDoc : currentUser.getAreas()) {
            if (areaDoc.getCode().equals(areaCode)) {
                allow = true;
                break;
            }
        }
        return allow;
    }

    @Override
    public String createNewBusinessPremises(BusinessPremisesSaveRequest businessPremisesSaveRequest) throws Exception {
        //check valid area and business type code
        AreaDoc area = areaRepository.findByCode(businessPremisesSaveRequest.getAreaCode());
        if (area == null) throw new Exception("Không có khu vực có mã " + businessPremisesSaveRequest.getAreaCode());

        BusinessTypeDoc businessType = businessTypeRepository.findByCode(businessPremisesSaveRequest.getBusinessTypeCode());
        if (businessType == null)
            throw new Exception("Không có loại hình có mã " + businessPremisesSaveRequest.getBusinessTypeCode());

        //current user can only do stuff in his assigned area(s)
        Boolean allow = checkIfUserAndPremisesHaveSameArea(businessPremisesSaveRequest.getAreaCode());
        if (!allow)
            throw new Exception("Người dùng không thể thực hiện thao tác ở khu vực " + businessPremisesSaveRequest.getAreaCode());

        BusinessPremisesDoc businessPremisesDoc = new BusinessPremisesDoc();
        BeanUtils.copyProperties(businessPremisesSaveRequest, businessPremisesDoc);
        businessPremisesDoc.setBusinessType(businessType);
        businessPremisesDoc.setAreaDoc(area);
        businessPremisesDoc.setWarningStatus(Commons.WARNING_STATUS_OK);
        businessPremisesDoc.setCreateAt(LocalDateTime.now());
        UserDoc currentUser = WebUtils.getCurrentUser().getUser();
        businessPremisesDoc.setCreateBy(currentUser.getEmail());

        businessPremisesDoc.setId(generatorSeqService.getNextSequenceId(businessPremisesDoc.SEQUENCE_NAME));
        businessPremisesRepository.save(businessPremisesDoc);
        return "Lưu cơ sở kinh doanh thành công";
    }

    @Override
    public String addLicensesToPremises(ListLicenseRequest listLicenseRequest, Long premisesId) throws Exception {
        if (listLicenseRequest.getLicenses().isEmpty()) throw new Exception("Không tìm thấy giấy chứng nhận để lưu");

        try {
            listLicenseRequest.validateInput();
        } catch (Exception e) {
            throw e;
        }

        Optional<BusinessPremisesDoc> optionalPremises = businessPremisesRepository.findById(premisesId);
        if (!optionalPremises.isPresent()) throw new Exception("Id cơ sở không hợp lệ");

        BusinessPremisesDoc businessPremisesDoc = optionalPremises.get();
        //current user can only do stuff in his assigned area(s)
        checkIfUserAndPremisesHaveSameArea(businessPremisesDoc.getAreaDoc().getCode());

        //lưu license vào DB trước
        List<LicenseDoc> licenseDocList = new ArrayList<>();
        for (LicenseSaveRequest licenseSaveRequest : listLicenseRequest.getLicenses()) {
            try {
                licenseSaveRequest.validateInput();
            } catch (Exception e) {
                throw e;
            }
            if (licenseRepository.existsByRegno(licenseSaveRequest.getRegno())) {
                throw new Exception("Số giấy chứng nhận đã tồn tại");
            }
            LicenseDoc licenseDoc = new LicenseDoc();
            BeanUtils.copyProperties(licenseSaveRequest, licenseDoc);
            LocalDateTime createdDate = utils.convertStringToLocalDateTime01(licenseSaveRequest.getCreatedDate());
            LocalDateTime expirationDate = utils.convertStringToLocalDateTime01(licenseSaveRequest.getExpirationDate());
            licenseDoc.setCreatedDate(createdDate);
            licenseDoc.setExpirationDate(expirationDate);
            if (EProvider.QUAN.getCode().equals(licenseSaveRequest.getProviderCode())) {
                licenseDoc.setProvider(EProvider.QUAN);
            }
            if (EProvider.THANH_PHO.getCode().equals(licenseSaveRequest.getProviderCode())) {
                licenseDoc.setProvider(EProvider.THANH_PHO);
            }
            if (ELicenseType.FOOD_SAFETY_CERTIFICATE.getCode().equals(licenseSaveRequest.getLicenseTypeCode())) {
                licenseDoc.setLicenseType(ELicenseType.FOOD_SAFETY_CERTIFICATE);
            }
            if (ELicenseType.BUSINESS_LICENSE.getCode().equals(licenseSaveRequest.getLicenseTypeCode())) {
                licenseDoc.setLicenseType(ELicenseType.BUSINESS_LICENSE);
            }
            licenseDoc.setId(generatorSeqService.getNextSequenceId(licenseDoc.SEQUENCE_NAME));
            licenseDocList.add(licenseDoc);
        }
        licenseRepository.saveAll(licenseDocList);

        //update thông tin license trong business premises
        licenseDocList.stream().forEach(item -> businessPremisesDoc.getLicenses().add(item));
        businessPremisesDoc.updateLicenseInfo(licenseDocList);
        businessPremisesDoc.setUpdateAt(LocalDateTime.now());
        UserDoc currentUser = WebUtils.getCurrentUser().getUser();
        businessPremisesDoc.setUpdateBy(currentUser.getEmail());
        businessPremisesRepository.save(businessPremisesDoc);

        return "Thêm giấy chứng nhận cho cơ sở thành công";
    }

    @Override
    public String addPersonToPremises(ListPersonRequest listPersonRequest, Long premisesId) throws Exception {
        if (listPersonRequest.getPersonSaveRequests().isEmpty())
            throw new Exception("Không tìm thấy chủ/ quản lý để lưu");

        try {
            listPersonRequest.validateInput();
        } catch (Exception e) {
            throw e;
        }

        Optional<BusinessPremisesDoc> optionalPremises = businessPremisesRepository.findById(premisesId);
        if (!optionalPremises.isPresent()) throw new Exception("Id cơ sở không hợp lệ");

        BusinessPremisesDoc businessPremisesDoc = optionalPremises.get();
        //current user can only do stuff in his assigned area(s)
        checkIfUserAndPremisesHaveSameArea(businessPremisesDoc.getAreaDoc().getCode());

        //lưu person vào DB trước
        List<PersonDoc> personDocList = new ArrayList<>();
        for (PersonSaveRequest personSaveRequest : listPersonRequest.getPersonSaveRequests()) {
            try {
                personSaveRequest.validateInput();
            } catch (Exception e) {
                throw e;
            }
            if (personRepository.existsByEmail(personSaveRequest.getEmail())) {
                throw new Exception("Email đã tồn tại : " + personSaveRequest.getEmail());
            }
            if (personRepository.existsByPhoneNumber(personSaveRequest.getPhoneNumber())) {
                throw new Exception("Sđt đã tồn tại : " + personSaveRequest.getPhoneNumber());
            }
            if (personRepository.existsBySocialSecurityNum(personSaveRequest.getSocialSecurityNum())) {
                throw new Exception("Số căn cước đã tồn tại : " + personSaveRequest.getSocialSecurityNum());
            }

            PersonDoc personDoc = new PersonDoc();
            BeanUtils.copyProperties(personSaveRequest, personDoc);
            LocalDateTime dob = utils.convertStringToLocalDateTime01(personSaveRequest.getDob());
            personDoc.setDob(dob);

            if (EPosition.OWNER.getCode().equals(personSaveRequest.getPositionCode())) {
                personDoc.setPosition(EPosition.OWNER);
            }
            if (EPosition.MANAGER.getCode().equals(personSaveRequest.getPositionCode())) {
                personDoc.setPosition(EPosition.MANAGER);
            }
            personDoc.setId(generatorSeqService.getNextSequenceId(personDoc.SEQUENCE_NAME));
            personDocList.add(personDoc);
        }
        personRepository.saveAll(personDocList);

        //update thông tin license trong business premises
        personDocList.stream().forEach(item -> businessPremisesDoc.getPeople().add(item));
        businessPremisesDoc.updatePeopleInfor(personDocList);
        businessPremisesDoc.setUpdateAt(LocalDateTime.now());
        UserDoc currentUser = WebUtils.getCurrentUser().getUser();
        businessPremisesDoc.setUpdateBy(currentUser.getEmail());
        businessPremisesRepository.save(businessPremisesDoc);

        return "Thêm chủ/ quản lý cho cơ sở thành công";
    }

    @Override
    public String updateInspect(InspectRequest inspectRequest, Long premisesId) throws Exception {
        Optional<BusinessPremisesDoc> optionalPremises = businessPremisesRepository.findById(premisesId);
        if (!optionalPremises.isPresent()) throw new Exception("Id cơ sở không hợp lệ");

        BusinessPremisesDoc businessPremisesDoc = optionalPremises.get();
        //current user can only do stuff in his assigned area(s)
        checkIfUserAndPremisesHaveSameArea(businessPremisesDoc.getAreaDoc().getCode());

        LocalDateTime lastInspectDate = utils.convertStringToLocalDateTime01(inspectRequest.getLastInspectDate());
        businessPremisesDoc.setLastInspectDate(lastInspectDate);
        if (inspectRequest.getWarningContent().equals("") || inspectRequest.getWarningContent() == null) {
            businessPremisesDoc.setWarningStatus(Commons.WARNING_STATUS_OK);
            businessPremisesDoc.setWarningContent(inspectRequest.getWarningContent());
            businessPremisesRepository.save(businessPremisesDoc);
        } else {
            businessPremisesDoc.setWarningContent(inspectRequest.getWarningContent());
            businessPremisesDoc.setWarningStatus(Commons.WARNING_STATUS_PROBLEM);
            businessPremisesRepository.save(businessPremisesDoc);
        }
        return "Đã cập nhật kết quả thanh tra";
    }

    @Override
    public BusinessPremisesDto getDeailBusinessPremises(Long premisesId) throws Exception {
        Optional<BusinessPremisesDoc> optionalPremises = businessPremisesRepository.findById(premisesId);
        if (!optionalPremises.isPresent()) throw new Exception("Id cơ sở không hợp lệ");

        BusinessPremisesDoc businessPremisesDoc = optionalPremises.get();

        BusinessPremisesDto businessPremisesDto = new BusinessPremisesDto();

        businessPremisesDto.setBusinessType(businessPremisesDoc.getBusinessType().getName());
        businessPremisesDto.setArea(businessPremisesDoc.getAreaDoc().getName());
        if (businessPremisesDoc.getFoodSafetyCertificateStartDate() != null)
            businessPremisesDto.setFoodSafetyCertificateStartDate(utils.convertDateToString(businessPremisesDoc.getFoodSafetyCertificateStartDate()));
        if (businessPremisesDoc.getFoodSafetyCertificateEndDate() != null)
            businessPremisesDto.setFoodSafetyCertificateEndDate(utils.convertDateToString(businessPremisesDoc.getFoodSafetyCertificateEndDate()));
        if (businessPremisesDoc.getLastInspectDate() != null)
            businessPremisesDto.setLastInspectDate(utils.convertDateToString(businessPremisesDoc.getLastInspectDate()));

        if (!businessPremisesDoc.getPeople().isEmpty()) {
            PersonDto personDto;
            for (PersonDoc personDoc : businessPremisesDoc.getPeople()) {
                personDto = new PersonDto();
                BeanUtils.copyProperties(personDoc, personDto);
                personDto.setDob(utils.convertDateToString(personDoc.getDob()));
                personDto.setPosition(personDoc.getPosition().getName());
                businessPremisesDto.getPeople().add(personDto);
            }
        }

        if (!businessPremisesDoc.getLicenses().isEmpty()) {
            LicenseDto licenseDto;
            for (LicenseDoc licenseDoc : businessPremisesDoc.getLicenses()) {
                licenseDto = new LicenseDto();
                BeanUtils.copyProperties(licenseDoc, licenseDto);
                licenseDto.setCreatedDate(utils.convertDateToString(licenseDoc.getCreatedDate()));
                licenseDto.setExpirationDate(utils.convertDateToString(licenseDoc.getExpirationDate()));
                licenseDto.setProvider(licenseDoc.getProvider().getName());
                licenseDto.setLicenseType(licenseDoc.getLicenseType().getName());
                businessPremisesDto.getLicenses().add(licenseDto);
            }
        }
        businessPremisesDoc.updateLicenseAndCertificateStatus();
        BeanUtils.copyProperties(businessPremisesDoc, businessPremisesDto);
        return businessPremisesDto;
    }

    @Override
    public PagedListHolder<BusinessPremisesSearchDto> search(Integer page, Integer size, String sortby, String keyword, String area, String businessType, String foodSafetyCertificateProvidedBy, Integer licenseStatus, Integer certificateStatus, Integer warningStatus) {
        Query query = new Query();

        if (!StringUtils.isEmpty(keyword.toLowerCase().trim())) {
            Criteria orCriterias = new Criteria();
            List<Criteria> orExpressions  = new ArrayList<>();
            orExpressions.add(Criteria.where("name").regex(".*" + keyword.toLowerCase().trim() + ".*", "i"));
            orExpressions.add(Criteria.where("addressDetail").regex(".*" + keyword.toLowerCase().trim() + ".*", "i"));
            orExpressions.add(Criteria.where("addressGeneral").regex(".*" + keyword.toLowerCase().trim() + ".*", "i"));
            orExpressions.add(Criteria.where("ownerInfo").regex(".*" + keyword.toLowerCase().trim() + ".*", "i"));
            orExpressions.add(Criteria.where("managerInfo").regex(".*" + keyword.toLowerCase().trim() + ".*", "i"));
            query.addCriteria(orCriterias.orOperator(orExpressions.toArray(new Criteria[orExpressions.size()])));
        }

        if (!StringUtils.isEmpty(area.toLowerCase().trim())) {
            AreaDoc areaDoc = areaRepository.findByCode(area);
            query.addCriteria(Criteria.where("areaDoc").is(areaDoc));
        }

        if (!StringUtils.isEmpty(businessType.toLowerCase().trim())) {
            BusinessTypeDoc businessTypeDoc = businessTypeRepository.findByCode(businessType);
            query.addCriteria(Criteria.where("businessType").is(businessTypeDoc));
        }

        if (!StringUtils.isEmpty(foodSafetyCertificateProvidedBy.trim())) {
            if(EProvider.QUAN.getCode().equals(foodSafetyCertificateProvidedBy))
                query.addCriteria(Criteria.where("foodSafetyCertificateProvidedBy").is(EProvider.QUAN));
            else if(EProvider.THANH_PHO.getCode().equals(foodSafetyCertificateProvidedBy))
                query.addCriteria(Criteria.where("foodSafetyCertificateProvidedBy").is(EProvider.THANH_PHO));
        }

        if (licenseStatus != 2) {
            query.addCriteria(Criteria.where("resolveStatus").is(licenseStatus));
        }

        if (certificateStatus != 2) {
            query.addCriteria(Criteria.where("resolveStatus").is(certificateStatus));
        }

        if (warningStatus != 2) {
            query.addCriteria(Criteria.where("resolveStatus").is(warningStatus));
        }

        List<BusinessPremisesDoc> queryResults = mongoTemplate.find(query, BusinessPremisesDoc.class);

        List<BusinessPremisesSearchDto> returnResult = new ArrayList<>();
        BusinessPremisesSearchDto businessPremisesSearchDto;
        for(BusinessPremisesDoc businessPremisesDoc : queryResults){
            businessPremisesSearchDto = new BusinessPremisesSearchDto();
            BeanUtils.copyProperties(businessPremisesDoc, businessPremisesSearchDto);
            businessPremisesSearchDto.setBusinessType(businessPremisesDoc.getBusinessType().getName());
            businessPremisesSearchDto.setArea(businessPremisesDoc.getAreaDoc().getName());
            returnResult.add(businessPremisesSearchDto);
        }

        PagedListHolder pagable = new PagedListHolder(returnResult);

        MutableSortDefinition mutableSortDefinition = new MutableSortDefinition();
        mutableSortDefinition.setAscending(true);
        mutableSortDefinition.setIgnoreCase(true);
        mutableSortDefinition.setProperty(sortby);

        pagable.setSort(mutableSortDefinition);
        pagable.resort();

        pagable.setPageSize(size);
        pagable.setPage(page);
        return pagable;
    }
}
