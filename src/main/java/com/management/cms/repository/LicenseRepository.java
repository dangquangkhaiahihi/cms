package com.management.cms.repository;

import com.management.cms.model.enitity.LicenseDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LicenseRepository extends MongoRepository<LicenseDoc, Long> {
    Boolean existsByRegno(String regno);
}
