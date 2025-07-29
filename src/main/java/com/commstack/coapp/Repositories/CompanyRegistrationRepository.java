package com.commstack.coapp.Repositories;

import com.commstack.coapp.Models.CompanyRegistration;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CompanyRegistrationRepository extends MongoRepository<CompanyRegistration, String> {
    List<CompanyRegistration> findByStatus(String status);
}
