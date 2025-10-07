package com.commstack.coapp.Repositories;

import com.commstack.coapp.Models.SectionMapping;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SectionMappingRepository extends MongoRepository<SectionMapping, String> {
    Optional<SectionMapping> findByName(String name);
}
