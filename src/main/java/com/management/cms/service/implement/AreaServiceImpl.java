package com.management.cms.service.implement;

import com.management.cms.constant.Commons;
import com.management.cms.model.dto.UserDto;
import com.management.cms.model.enitity.AreaDoc;
import com.management.cms.model.request.AreaSaveRequest;
import com.management.cms.model.request.AreaSearchRequest;
import com.management.cms.repository.AreaRepository;
import com.management.cms.service.AreaService;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AreaServiceImpl implements AreaService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private GeneratorSeqService generatorSeqService;

    @Autowired
    private AreaRepository areaRepository;

    @Override
    public AreaDoc createNewArea(AreaSaveRequest areaSaveRequest) throws Exception{
        //check đã có area code trong DB chưa
        if (Boolean.TRUE.equals(areaRepository.existsByCode(areaSaveRequest.getCode()))){
            throw new Exception("Mã khu vực đã tồn tại");
        }
        try{
            areaSaveRequest.vailidateInput();
        }catch(Exception e){
            throw e;
        }

        //tạo đối tượng
        AreaDoc areaDoc = new AreaDoc();
        BeanUtils.copyProperties(areaSaveRequest,areaDoc);

        areaDoc.setId(generatorSeqService.getNextSequenceId(areaDoc.SEQUENCE_NAME));

        //set status ACTIVE cho nó
        areaDoc.setStatus(Commons.STATUS_ACTIVE);

        //save nó vào DB
        areaRepository.save(areaDoc);
        return areaDoc;
    }

    @Override
    public AreaDoc editArea(AreaSaveRequest areaSaveRequest, Long id) throws Exception{
        //không được thay đổi id
        if(id != areaSaveRequest.getId()) throw new Exception("Không được thay đổi id");
        try{
            areaSaveRequest.vailidateInput();
        }catch(Exception e){
            throw e;
        }
        //check id truyền vào có trong DB ko
        Optional<AreaDoc> optional = areaRepository.findById(id);
        if (!optional.isPresent()){
            throw new Exception("Id không hợp lệ.");
        }
        AreaDoc areaDoc = optional.get();

        //nếu mã khu vực thay đổi thì check cái mã mới đã có trong DB chưa
        if (!areaSaveRequest.getCode().equals(areaDoc.getCode())
                && Boolean.TRUE.equals(areaRepository.existsByCode(areaSaveRequest.getCode()))){
            throw new Exception("Mã khu vực đã tồn tại");
        }

        BeanUtils.copyProperties(areaSaveRequest,areaDoc);

        areaRepository.save(areaDoc);
        return areaDoc;
    }

    @Override
    public PagedListHolder<AreaDoc> searchAllArea(AreaSearchRequest areaSearchRequest, Integer page, Integer size, String sortby) {
        Query query = new Query();

        if (!StringUtils.isEmpty(areaSearchRequest.getKeyword().toLowerCase().trim())) {
            Criteria orCriterias = new Criteria();
            List<Criteria> orExpressions  = new ArrayList<>();
            orExpressions.add(Criteria.where("code").regex(".*" + areaSearchRequest.getKeyword().toLowerCase().trim() + ".*", "i"));
            orExpressions.add(Criteria.where("name").regex(".*" + areaSearchRequest.getKeyword().toLowerCase().trim() + ".*", "i"));
            query.addCriteria(orCriterias.orOperator(orExpressions.toArray(new Criteria[orExpressions.size()])));
        }

        if (areaSearchRequest.getStatus() != 2) {
            query.addCriteria(Criteria.where("status").is(areaSearchRequest.getStatus()));
        }

        List<AreaDoc> queryResults = mongoTemplate.find(query, AreaDoc.class);

        PagedListHolder pagable = new PagedListHolder(queryResults);

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

    @Override
    public AreaDoc getAreaDetailById(Long id) throws Exception{
        Optional<AreaDoc> optional = areaRepository.findById(id);
        if (!optional.isPresent()) {
            throw new Exception("Id không hợp lệ.");
        }
        AreaDoc areaDoc = optional.get();
        return areaDoc;
    }

    @Override
    public AreaDoc lockAndUnlockById(Long id) throws Exception {
        Optional<AreaDoc> optional = areaRepository.findById(id);
        if (!optional.isPresent()) {
            throw new Exception("Id không hợp lệ.");
        }
        AreaDoc areaDoc = optional.get();
        if(areaDoc.getStatus() == Commons.STATUS_ACTIVE){
            areaDoc.setStatus(Commons.STATUS_INACTIVE);
        }else{
            areaDoc.setStatus(Commons.STATUS_ACTIVE);
        }
        areaRepository.save(areaDoc);
        return areaDoc;
    }

    @Override
    public List<AreaDoc> getAllActiveAreas() {
        Query query = new Query();

        query.addCriteria(Criteria.where("status").is(1));

        List<AreaDoc> queryResults = mongoTemplate.find(query, AreaDoc.class);

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
