package com.commstack.coapp.Repositories;

import com.commstack.coapp.Models.SecurityCompany;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SecurityCompanyRepository extends MongoRepository<SecurityCompany, String> {
    List<SecurityCompany> findByStatus(String status);

    SecurityCompany findByEmailAddress(String emailAddress);

    SecurityCompany findByBpNumber(String bpNumber);
}
