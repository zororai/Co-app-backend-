package com.commstack.coapp.Repositories;

import com.commstack.coapp.Models.ShaftAssignment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShaftAssignmentRepository extends MongoRepository<ShaftAssignment, String> {
    ShaftAssignment findByShaftNumbers(String shaftNumbers);
}
