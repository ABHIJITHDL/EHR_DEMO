package com.ehr.EHR.repository;

import com.ehr.EHR.model.EHR;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EHRRepository extends MongoRepository<EHR, String> {
}
