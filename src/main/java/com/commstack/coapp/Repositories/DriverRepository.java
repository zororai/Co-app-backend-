package com.commstack.coapp.Repositories;

import com.commstack.coapp.Models.Driver;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DriverRepository extends MongoRepository<Driver, String> {
    List<Driver> findByStatus(String status);

    Driver findByEmail(String email);

    Driver findByIdNumber(String idNumber);

    Driver findByLicenseNumber(String licenseNumber);

    List<Driver> findByCompanyName(String companyName);
}
