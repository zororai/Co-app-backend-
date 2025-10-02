package com.commstack.coapp.Repositories;

import com.commstack.coapp.Models.SectionMapping;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SectionMappingRepository extends MongoRepository<SectionMapping, String> {
    // Add custom queries later if needed
}
