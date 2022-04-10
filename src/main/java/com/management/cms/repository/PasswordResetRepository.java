package com.management.cms.repository;

import com.management.cms.model.enitity.PasswordResetDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PasswordResetRepository extends MongoRepository<PasswordResetDoc, Long> {
}
