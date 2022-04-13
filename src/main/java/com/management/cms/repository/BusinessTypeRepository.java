package com.management.cms.repository;

import com.management.cms.model.enitity.BusinessTypeDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BusinessTypeRepository extends MongoRepository<BusinessTypeDoc,Long> {
    Boolean existsByCode(String code);
    BusinessTypeDoc findByCode(String code);
}
