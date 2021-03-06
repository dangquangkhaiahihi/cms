package com.management.cms.repository;

import com.management.cms.model.enitity.RoleDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends MongoRepository<RoleDoc, Long> {
    Optional<RoleDoc> findByCode(String code);
}
