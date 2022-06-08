package com.management.cms.service.implement;

import com.management.cms.constant.Commons;
import com.management.cms.model.enitity.AreaDoc;
import com.management.cms.model.enitity.BusinessTypeDoc;
import com.management.cms.model.request.BusinessTypeSaveRequest;
import com.management.cms.model.request.BusinessTypeSearchRequest;
import com.management.cms.repository.BusinessTypeRepository;
import com.management.cms.service.BusinessTypeService;
import com.management.cms.service.GeneratorSeqService;
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

import java.util.List;
import java.util.Optional;

@Service
public class BusinessTypeServiceImpl implements BusinessTypeService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private GeneratorSeqService generatorSeqService;

    @Autowired
    private BusinessTypeRepository businessTypeRepository;

    @Override
    public BusinessTypeDoc createNewType(BusinessTypeSaveRequest businessTypeSaveRequest) throws Exception {
        //check đã có area code trong DB chưa
        if (Boolean.TRUE.equals(businessTypeRepository.existsByCode(businessTypeSaveRequest.getCode()))){
            throw new Exception("Mã loại hình kinh doanh đã tồn tại");
        }

        //tạo đối tượng
        BusinessTypeDoc businessTypeDoc = new BusinessTypeDoc();
        BeanUtils.copyProperties(businessTypeSaveRequest,businessTypeDoc);

        businessTypeDoc.setId(generatorSeqService.getNextSequenceId(businessTypeDoc.SEQUENCE_NAME));

        //set status ACTIVE cho nó
        businessTypeDoc.setStatus(Commons.STATUS_ACTIVE);

        //save nó vào DB
        businessTypeRepository.save(businessTypeDoc);
        return businessTypeDoc;
    }

    @Override
    public BusinessTypeDoc editType(BusinessTypeSaveRequest businessTypeSaveRequest, Long id) throws Exception {
        //không được thay đổi id
        if(id != businessTypeSaveRequest.getId()) throw new Exception("Không được thay đổi id");

        //check id truyền vào có trong DB ko
        Optional<BusinessTypeDoc> optional = businessTypeRepository.findById(id);
        if (!optional.isPresent()){
            throw new Exception("Id không hợp lệ.");
        }
        BusinessTypeDoc businessTypeDoc = optional.get();

        //nếu mã khu vực thay đổi thì check cái mã mới đã có trong DB chưa
        if (!businessTypeSaveRequest.getCode().equals(businessTypeDoc.getCode())
                && Boolean.TRUE.equals(businessTypeRepository.existsByCode(businessTypeSaveRequest.getCode()))){
            throw new Exception("Mã loại hình kinh doanh đã tồn tại");
        }

        BeanUtils.copyProperties(businessTypeSaveRequest,businessTypeDoc);

        businessTypeRepository.save(businessTypeDoc);
        return businessTypeDoc;
    }

    @Override
    public Page<BusinessTypeDoc> searchAllType(BusinessTypeSearchRequest businessTypeSearchRequest, Pageable pageable) {
        Query query = new Query();

        if (!StringUtils.isEmpty(businessTypeSearchRequest.getCode().toLowerCase().trim())) {
            query.addCriteria(Criteria.where("code").regex(".*"+businessTypeSearchRequest.getCode().toLowerCase().trim()+".*", "i"));
        }

        if (businessTypeSearchRequest.getStatus() != 2) {
            query.addCriteria(Criteria.where("status").is(businessTypeSearchRequest.getStatus()));
        }

        List<BusinessTypeDoc> queryResults = mongoTemplate.find(query, BusinessTypeDoc.class);

        Long total = Long.valueOf(queryResults.size());
        return new PageImpl<>(queryResults, pageable, total);
    }

    @Override
    public BusinessTypeDoc getTypeDetailById(Long id) throws Exception {
        Optional<BusinessTypeDoc> optional = businessTypeRepository.findById(id);
        if (!optional.isPresent()) {
            throw new Exception("Id không hợp lệ.");
        }
        BusinessTypeDoc businessTypeDoc = optional.get();
        return businessTypeDoc;
    }

    @Override
    public BusinessTypeDoc lockAndUnlockById(Long id) throws Exception {
        Optional<BusinessTypeDoc> optional = businessTypeRepository.findById(id);
        if (!optional.isPresent()) {
            throw new Exception("Id không hợp lệ.");
        }
        BusinessTypeDoc businessTypeDoc = optional.get();
        if(businessTypeDoc.getStatus() == Commons.STATUS_ACTIVE){
            businessTypeDoc.setStatus(Commons.STATUS_INACTIVE);
        }else{
            businessTypeDoc.setStatus(Commons.STATUS_ACTIVE);
        }
        businessTypeRepository.save(businessTypeDoc);
        return businessTypeDoc;
    }

    @Override
    public List<BusinessTypeDoc> getAllActiveBusinessType() {
        Query query = new Query();

        query.addCriteria(Criteria.where("status").is(1));

        List<BusinessTypeDoc> queryResults = mongoTemplate.find(query, BusinessTypeDoc.class);

        PagedListHolder pagable = new PagedListHolder(queryResults);

        MutableSortDefinition mutableSortDefinition = new MutableSortDefinition();
        mutableSortDefinition.setAscending(true);
        mutableSortDefinition.setIgnoreCase(true);
        mutableSortDefinition.setProperty("code");
        pagable.setSort(mutableSortDefinition);
        pagable.resort();
        return pagable.getPageList();
    }
}
