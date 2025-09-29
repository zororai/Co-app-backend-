package com.commstack.coapp.Repositories;

import com.commstack.coapp.Models.Section;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SectionRepository extends MongoRepository<Section, String> {
    List<Section> findByStatus(String status);

    List<Section> findByMinerId(String minerId);

    Section findBySectionNameAndMinerId(String sectionName, String minerId);

    List<Section> findByNumberOfShaft(String numberOfShaft);

    List<Section> findByIsActiveFalseAndStatus(String status);

    // Alternative query using @Query annotation for better compatibility
    @Query("{ 'isActive': false, 'status': ?0 }")
    List<Section> findDeactivatedSectionsByStatus(String status);
}
