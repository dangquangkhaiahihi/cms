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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
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

    public void checkIfUserAndPremisesHaveSameArea(String areaCode) throws Exception {
        UserDoc currentUser = WebUtils.getCurrentUser().getUser();
        Boolean allow = false;
        for (AreaDoc areaDoc : currentUser.getAreas()) {
            if (areaDoc.getCode().equals(areaCode)) {
                allow = true;
                break;
            }
        }
        if (allow == false) {
            throw new Exception("Người dùng không thể thực hiện thao tác ở khu vực " + areaCode);
        }
    }

    @Override
    public String createNewBusinessPremises(BusinessPremisesSaveRequest businessPremisesSaveRequest) throws Exception {
        //validate input
        try {
            businessPremisesSaveRequest.validateInput();
        } catch (Exception e) {
            throw e;
        }
        //check valid area and business type code
        AreaDoc area = areaRepository.findByCode(businessPremisesSaveRequest.getAreaCode());
        if (area == null) throw new Exception("Không có khu vực có mã " + businessPremisesSaveRequest.getAreaCode());

        BusinessTypeDoc businessType = businessTypeRepository.findByCode(businessPremisesSaveRequest.getBusinessTypeCode());
        if (businessType == null)
            throw new Exception("Không có loại hình có mã " + businessPremisesSaveRequest.getBusinessTypeCode());

        //current user can only do stuff in his assigned area(s)
        try {
            checkIfUserAndPremisesHaveSameArea(businessPremisesSaveRequest.getAreaCode());
        } catch (Exception e) {
            throw e;
        }

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

    public String editBusinessPremises(BusinessPremisesSaveRequest businessPremisesSaveRequest) throws Exception {
        //validate input
        try {
            businessPremisesSaveRequest.validateInput();
        } catch (Exception e) {
            throw e;
        }

        //current user can only do stuff in his assigned area(s)
        try {
            checkIfUserAndPremisesHaveSameArea(businessPremisesSaveRequest.getAreaCode());
        } catch (Exception e) {
            throw e;
        }

        BusinessPremisesDoc businessPremisesDoc = businessPremisesRepository.findById(businessPremisesSaveRequest.getId()).get();
        BeanUtils.copyProperties(businessPremisesSaveRequest, businessPremisesDoc);

        AreaDoc area = new AreaDoc();
        BusinessTypeDoc businessType = new BusinessTypeDoc();

        if (!businessPremisesDoc.getAreaDoc().getCode().equals(businessPremisesSaveRequest.getAreaCode())) {
            area = areaRepository.findByCode(businessPremisesSaveRequest.getAreaCode());
            if (area == null)
                throw new Exception("Không có khu vực có mã " + businessPremisesSaveRequest.getAreaCode());
            else {
                businessPremisesDoc.setAreaDoc(area);
            }
        }
        if (!businessPremisesDoc.getBusinessType().getCode().equals(businessPremisesSaveRequest.getBusinessTypeCode())) {
            businessType = businessTypeRepository.findByCode(businessPremisesSaveRequest.getBusinessTypeCode());
            if (businessType == null)
                throw new Exception("Không có loại hình có mã " + businessPremisesSaveRequest.getBusinessTypeCode());
            else {
                businessPremisesDoc.setBusinessType(businessType);
            }
        }
        businessPremisesDoc.setWarningStatus(Commons.WARNING_STATUS_OK);
        businessPremisesDoc.setUpdateAt(LocalDateTime.now());
        UserDoc currentUser = WebUtils.getCurrentUser().getUser();
        businessPremisesDoc.setCreateBy(currentUser.getEmail());

        businessPremisesRepository.save(businessPremisesDoc);
        return "Chỉnh sửa cơ sở kinh doanh thành công";
    }

    @Override
    public String addLicensesToPremises(ListLicenseRequest listLicenseRequest, Long premisesId) throws Exception {
        try {
            listLicenseRequest.validateInput();
        } catch (Exception e) {
            throw e;
        }

        Optional<BusinessPremisesDoc> optionalPremises = businessPremisesRepository.findById(premisesId);
        if (!optionalPremises.isPresent()) throw new Exception("Id cơ sở không hợp lệ");

        BusinessPremisesDoc businessPremisesDoc = optionalPremises.get();
        //current user can only do stuff in his assigned area(s)
        try {
            checkIfUserAndPremisesHaveSameArea(businessPremisesDoc.getAreaDoc().getCode());
        } catch (Exception e) {
            throw e;
        }

        //lưu license vào DB trước
        List<LicenseDoc> licenseDocListAddNew = new ArrayList<>();
        List<LicenseDoc> licenseDocListUpdate = new ArrayList<>();
        List<LicenseDoc> licenseDocListDelete = new ArrayList<>();
        List<Long> licenseIdsInReq = new ArrayList<>();
        String certifivateProvidedBy = "";

        for (LicenseSaveRequest licenseSaveRequest : listLicenseRequest.getLicenses()) {
            try {
                licenseSaveRequest.validateInput();
            } catch (Exception e) {
                throw e;
            }

            LicenseDoc licenseDoc = new LicenseDoc();
            BeanUtils.copyProperties(licenseSaveRequest, licenseDoc);
            LocalDateTime createdDate;
            LocalDateTime expirationDate;
            try {
                createdDate = utils.convertStringToLocalDateTime01(licenseSaveRequest.getCreatedDate());
                expirationDate = utils.convertStringToLocalDateTime01(licenseSaveRequest.getExpirationDate());
            } catch (Exception e) {
                throw new Exception("Ngày không hợp lệ");
            }
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
                certifivateProvidedBy = ELicenseType.FOOD_SAFETY_CERTIFICATE.getCode();
            }
            if (ELicenseType.BUSINESS_LICENSE.getCode().equals(licenseSaveRequest.getLicenseTypeCode())) {
                licenseDoc.setLicenseType(ELicenseType.BUSINESS_LICENSE);
            }

            if (licenseSaveRequest.getId() == null) {
                if (licenseRepository.existsByRegno(licenseSaveRequest.getRegno())) {
                    throw new Exception("Số giấy chứng nhận đã tồn tại");
                }
                licenseDoc.setId(generatorSeqService.getNextSequenceId(licenseDoc.SEQUENCE_NAME));
                licenseDocListAddNew.add(licenseDoc);
            } else {
                licenseIdsInReq.add(licenseSaveRequest.getId());
                if (!licenseRepository.findById(licenseSaveRequest.getId()).get().getRegno().equals(licenseSaveRequest.getRegno())) {
                    if (licenseRepository.existsByRegno(licenseSaveRequest.getRegno())) {
                        throw new Exception("Số giấy chứng nhận đã tồn tại");
                    }
                }
                licenseDocListUpdate.add(licenseDoc);
            }
        }

        for (LicenseDoc licenseDoc : businessPremisesDoc.getLicenses()) {
            if (!licenseIdsInReq.contains(licenseDoc.getId())) {
                licenseDocListDelete.add(licenseDoc);
            }
        }

        for (LicenseDoc licenseDoc : licenseDocListDelete) {
            businessPremisesDoc.getLicenses().remove(licenseDoc);
            licenseRepository.delete(licenseDoc);
        }

        licenseRepository.saveAll(licenseDocListAddNew);

        businessPremisesDoc.setFoodSafetyCertificateProvidedBy("");
        for (LicenseDoc licenseDoc : licenseDocListUpdate) {
            if (licenseDoc.getLicenseType().equals(ELicenseType.FOOD_SAFETY_CERTIFICATE)) {
                businessPremisesDoc.setFoodSafetyCertificateProvidedBy(certifivateProvidedBy);
            }
        }
        licenseRepository.saveAll(licenseDocListUpdate);

        //update thông tin license trong business premises
        licenseDocListAddNew.stream().forEach(item -> businessPremisesDoc.getLicenses().add(item));
        businessPremisesDoc.updateLicenseInfo(licenseDocListAddNew);
        businessPremisesDoc.updateLicenseInfo(licenseDocListUpdate);
        businessPremisesDoc.setUpdateAt(LocalDateTime.now());
        UserDoc currentUser = WebUtils.getCurrentUser().getUser();
        businessPremisesDoc.setUpdateBy(currentUser.getEmail());
        businessPremisesRepository.save(businessPremisesDoc);

        return "Thêm giấy chứng nhận cho cơ sở thành công";
    }

    @Override
    public String addPersonToPremises(ListPersonRequest listPersonRequest, Long premisesId) throws Exception {
        try {
            listPersonRequest.validateInput();
        } catch (Exception e) {
            throw e;
        }

        Optional<BusinessPremisesDoc> optionalPremises = businessPremisesRepository.findById(premisesId);
        if (!optionalPremises.isPresent()) throw new Exception("Id cơ sở không hợp lệ");

        BusinessPremisesDoc businessPremisesDoc = optionalPremises.get();
        //current user can only do stuff in his assigned area(s)
        try {
            checkIfUserAndPremisesHaveSameArea(businessPremisesDoc.getAreaDoc().getCode());
        } catch (Exception e) {
            throw e;
        }

        //lưu person vào DB trước
        List<PersonDoc> personDocListAddNew = new ArrayList<>();
        List<PersonDoc> personDocListUpdate = new ArrayList<>();
        List<PersonDoc> personDocListDelete = new ArrayList<>();
        List<Long> peopleIdsInReq = new ArrayList<>();

        for (PersonSaveRequest personSaveRequest : listPersonRequest.getPersonSaveRequests()) {
            try {
                personSaveRequest.validateInput();
            } catch (Exception e) {
                throw e;
            }

            boolean checkEmail = true;
            boolean checkPhoneNum = true;
            boolean checkSsn = true;

            for (PersonDoc personDoc : businessPremisesDoc.getPeople()) {
                if (personDoc.getId().equals(personSaveRequest.getId()) && personDoc.getEmail().equals(personSaveRequest.getEmail())) {
                    checkEmail = false;
                }
                if (personDoc.getId().equals(personSaveRequest.getId()) && personDoc.getPhoneNumber().equals(personSaveRequest.getPhoneNumber())) {
                    checkPhoneNum = false;
                }
                if (personDoc.getId().equals(personSaveRequest.getId()) && personDoc.getSocialSecurityNum().equals(personSaveRequest.getSocialSecurityNum())) {
                    checkSsn = false;
                }
            }

            if (checkEmail == true) {
                if (personRepository.existsByEmail(personSaveRequest.getEmail())) {
                    throw new Exception("Email đã tồn tại : " + personSaveRequest.getEmail());
                }
            }
            if (checkPhoneNum == true) {
                if (personRepository.existsByPhoneNumber(personSaveRequest.getPhoneNumber())) {
                    throw new Exception("Số điện thoại đã tồn tại : " + personSaveRequest.getPhoneNumber());
                }
            }
            if (checkSsn == true) {
                if (personRepository.existsBySocialSecurityNum(personSaveRequest.getSocialSecurityNum())) {
                    throw new Exception("Số căn cước đã tồn tại : " + personSaveRequest.getSocialSecurityNum());
                }
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
            if (personSaveRequest.getId() == null) {
                personDoc.setId(generatorSeqService.getNextSequenceId(personDoc.SEQUENCE_NAME));
                personDocListAddNew.add(personDoc);
            } else {
                peopleIdsInReq.add(personSaveRequest.getId());
                personDocListUpdate.add(personDoc);
            }
        }
        for (PersonDoc personDoc : businessPremisesDoc.getPeople()) {
            if (!peopleIdsInReq.contains(personDoc.getId())) {
                personDocListDelete.add(personDoc);
            }
        }

        for (PersonDoc personDoc : personDocListDelete) {
            if (personDoc.getPosition().equals(EPosition.MANAGER)) {
                businessPremisesDoc.setManagerInfo("");
            } else if (personDoc.getPosition().equals(EPosition.OWNER)) {
                businessPremisesDoc.setOwnerInfo("");
            }
            businessPremisesDoc.getPeople().remove(personDoc);
            personRepository.delete(personDoc);
        }
        personRepository.saveAll(personDocListAddNew);
        //update
        businessPremisesDoc.setOwnerInfo("");
        businessPremisesDoc.setManagerInfo("");
        for (PersonDoc personDoc : personDocListUpdate) {
            if (personDoc.getPosition().equals(EPosition.MANAGER)) {
                businessPremisesDoc.setManagerInfo(personDoc.getFirstName() + " " + personDoc.getLastName() + "\n" + personDoc.getPhoneNumber());
            } else if (personDoc.getPosition().equals(EPosition.OWNER)) {
                businessPremisesDoc.setOwnerInfo(personDoc.getFirstName() + " " + personDoc.getLastName() + "\n" + personDoc.getPhoneNumber());
            }
        }
        personRepository.saveAll(personDocListUpdate);

        //update thông tin license trong business premises
        personDocListAddNew.stream().forEach(item -> businessPremisesDoc.getPeople().add(item));
        businessPremisesDoc.updatePeopleInfor(personDocListAddNew);
        businessPremisesDoc.updatePeopleInfor(personDocListUpdate);
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
        try {
            checkIfUserAndPremisesHaveSameArea(businessPremisesDoc.getAreaDoc().getCode());
        } catch (Exception e) {
            throw e;
        }

        if (inspectRequest.getInspectDate() != null) {
            businessPremisesDoc.setLastInspectDate(utils.convertStringToLocalDateTime01(inspectRequest.getInspectDate()));
        }


        if (inspectRequest.getWarningStatus() == Commons.WARNING_STATUS_OK) {
            businessPremisesDoc.setWarningStatus(Commons.WARNING_STATUS_OK);
            businessPremisesDoc.setInspectDate(null);
            businessPremisesDoc.setWarningContent(inspectRequest.getWarningContent());
        }
        if (inspectRequest.getWarningStatus() == Commons.WARNING_STATUS_PROBLEM) {
            businessPremisesDoc.setWarningStatus(Commons.WARNING_STATUS_PROBLEM);
            businessPremisesDoc.setInspectDate(utils.convertStringToLocalDateTime01(inspectRequest.getInspectDate()));
            businessPremisesDoc.setWarningContent(inspectRequest.getWarningContent());
        }
        businessPremisesRepository.save(businessPremisesDoc);
        return "Đã cập nhật kết quả thanh tra";
    }

    @Override
    public BusinessPremisesDto getDeailBusinessPremises(Long premisesId) throws Exception {
        Optional<BusinessPremisesDoc> optionalPremises = businessPremisesRepository.findById(premisesId);
        if (!optionalPremises.isPresent()) throw new Exception("Id cơ sở không hợp lệ");

        BusinessPremisesDoc businessPremisesDoc = optionalPremises.get();
        try {
            checkIfUserAndPremisesHaveSameArea(businessPremisesDoc.getAreaDoc().getCode());
        } catch (Exception e) {
            throw e;
        }
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
                personDto.setPosition(personDoc.getPosition().getCode());
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
                licenseDto.setProvider(licenseDoc.getProvider().getCode());
                licenseDto.setLicenseType(licenseDoc.getLicenseType().getCode());
                businessPremisesDto.getLicenses().add(licenseDto);
            }
        }
        businessPremisesDoc.updateLicenseAndCertificateStatus();
        if (businessPremisesDoc.getLastInspectDate() == null) {
            businessPremisesDto.setLastInspectDate("");
        } else {
            businessPremisesDto.setLastInspectDate(utils.convertDateToString(businessPremisesDoc.getLastInspectDate()));
        }

        if (businessPremisesDoc.getInspectDate() == null) {
            businessPremisesDto.setInspectDate("");
        } else {
            businessPremisesDto.setInspectDate(utils.convertDateToString(businessPremisesDoc.getInspectDate()));
        }

        BeanUtils.copyProperties(businessPremisesDoc, businessPremisesDto);
        return businessPremisesDto;
    }

    @Override
    public PagedListHolder<BusinessPremisesSearchDto> search(Integer page, Integer size, String sortby, String keyword, String area, String businessType, String foodSafetyCertificateProvidedBy, Integer licenseStatus, Integer certificateStatus, Integer warningStatus) {
        Query query = new Query();

        if (!StringUtils.isEmpty(keyword.toLowerCase().trim())) {
            Criteria orCriterias = new Criteria();
            List<Criteria> orExpressions = new ArrayList<>();
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
            if (EProvider.QUAN.getCode().equals(foodSafetyCertificateProvidedBy))
                query.addCriteria(Criteria.where("foodSafetyCertificateProvidedBy").is(EProvider.QUAN.getCode()));
            else if (EProvider.THANH_PHO.getCode().equals(foodSafetyCertificateProvidedBy))
                query.addCriteria(Criteria.where("foodSafetyCertificateProvidedBy").is(EProvider.THANH_PHO.getCode()));
        }

        if (warningStatus != 2) {
            query.addCriteria(Criteria.where("warningStatus").is(warningStatus));
        }

        List<BusinessPremisesDoc> queryResults = mongoTemplate.find(query, BusinessPremisesDoc.class);

        List<BusinessPremisesSearchDto> returnResult = new ArrayList<>();
        BusinessPremisesSearchDto businessPremisesSearchDto;
        for (BusinessPremisesDoc businessPremisesDoc : queryResults) {
            businessPremisesDoc.updateLicenseAndCertificateStatus();
            businessPremisesSearchDto = new BusinessPremisesSearchDto();
            BeanUtils.copyProperties(businessPremisesDoc, businessPremisesSearchDto);
            businessPremisesSearchDto.setBusinessType(businessPremisesDoc.getBusinessType().getName());
            businessPremisesSearchDto.setArea(businessPremisesDoc.getAreaDoc().getName());
            returnResult.add(businessPremisesSearchDto);
        }

        if (licenseStatus != 2) {
            Iterator<BusinessPremisesSearchDto> iterator = returnResult.iterator();
            while (iterator.hasNext()) {
                BusinessPremisesSearchDto item = iterator.next();
                if (!item.getLicenseStatus().equals(licenseStatus)) {
                    iterator.remove();
                }
            }
        }

        if (certificateStatus != 2) {
            Iterator<BusinessPremisesSearchDto> iterator = returnResult.iterator();
            while (iterator.hasNext()) {
                BusinessPremisesSearchDto item = iterator.next();
                if (!item.getCertificateStatus().equals(certificateStatus)) {
                    iterator.remove();
                }
            }
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
