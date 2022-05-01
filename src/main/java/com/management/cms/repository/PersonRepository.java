package com.management.cms.repository;

import com.management.cms.model.enitity.PersonDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PersonRepository extends MongoRepository<PersonDoc, Long> {
    Boolean existsByEmail(String email);

    Boolean existsByPhoneNumber(String phone);

    Boolean existsBySocialSecurityNum(String ssn);
}
