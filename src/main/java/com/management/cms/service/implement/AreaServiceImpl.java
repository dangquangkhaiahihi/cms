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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    public Page<AreaDoc> searchAllArea(AreaSearchRequest areaSearchRequest, Pageable pageable) {
        Query query = new Query();

        if (!StringUtils.isEmpty(areaSearchRequest.getCode().toLowerCase().trim())) {
            query.addCriteria(Criteria.where("code").regex(".*"+areaSearchRequest.getCode().toLowerCase().trim()+".*", "i"));
        }

        if (areaSearchRequest.getStatus() != 2) {
            query.addCriteria(Criteria.where("status").is(areaSearchRequest.getStatus()));
        }

        List<AreaDoc> queryResults = mongoTemplate.find(query, AreaDoc.class);

        Long total = Long.valueOf(queryResults.size());
        return new PageImpl<>(queryResults, pageable, total);
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
}
