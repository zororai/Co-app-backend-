package com.commstack.coapp.Repositories;

import com.commstack.coapp.Models.SecurityCompanyEmployee;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecurityCompanyEmployeeRepository extends MongoRepository<SecurityCompanyEmployee, String> {
    // You can add custom query methods here if needed
}
