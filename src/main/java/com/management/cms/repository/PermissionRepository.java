package com.management.cms.repository;

import com.management.cms.model.enitity.PermissionDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends MongoRepository<PermissionDoc, Long> {
}
