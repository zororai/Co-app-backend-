package com.commstack.coapp.Repositories;

import com.commstack.coapp.Models.AreaName;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AreaNameRepository extends MongoRepository<AreaName, String> {

    List<AreaName> findBySection(String section);

    List<AreaName> findByArea(String area);

    List<AreaName> findByNameContainingIgnoreCase(String name);

    List<AreaName> findBySectionAndArea(String section, String area);

    List<AreaName> findByCreatedBy(String createdBy);
}