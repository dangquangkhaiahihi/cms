package com.management.cms.repository;

import com.management.cms.model.enitity.AccessTokenMgo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccessTokenMgoRepository extends MongoRepository<AccessTokenMgo,Long> {
    AccessTokenMgo findByToken(String token);
    Optional<AccessTokenMgo> findByUserId(Long userId);
}
