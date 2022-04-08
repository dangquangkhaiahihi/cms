package com.management.cms.repository;

import com.management.cms.model.enitity.AreaDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AreaRepository extends MongoRepository<AreaDoc, Long> {
    Boolean existsByCode(String code);
}
