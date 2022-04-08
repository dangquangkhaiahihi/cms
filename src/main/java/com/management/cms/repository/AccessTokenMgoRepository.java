package com.management.cms.repository;

import com.management.cms.model.enitity.AccessTokenMgo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessTokenMgoRepository extends MongoRepository<AccessTokenMgo,Long> {
    AccessTokenMgo findById(String id);
    AccessTokenMgo findByToken(String token);
}
