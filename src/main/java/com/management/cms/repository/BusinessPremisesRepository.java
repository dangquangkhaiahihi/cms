package com.management.cms.repository;

import com.management.cms.model.enitity.BusinessPremisesDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BusinessPremisesRepository extends MongoRepository<BusinessPremisesDoc,Long> {
}
