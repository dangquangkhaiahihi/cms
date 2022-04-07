package com.management.cms.repository;

import com.management.cms.model.enitity.UserDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserDoc, Long> {
    Optional<UserDoc> findByEmail(String email);
}
