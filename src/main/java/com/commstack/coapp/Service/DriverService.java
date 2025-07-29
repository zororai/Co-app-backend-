package com.commstack.coapp.Service;

import com.commstack.coapp.Models.Driver;
import org.springframework.http.ResponseEntity;
import java.security.Principal;
import java.util.List;

public interface DriverService {
    ResponseEntity<String> create(Driver driver, Principal principal);

    List<Driver> getAll();

    Driver getById(String id);

    ResponseEntity<String> update(String id, Driver driver, Principal principal);

    ResponseEntity<String> delete(String id, Principal principal);

    ResponseEntity<String> approval(String id, Principal principal);

    ResponseEntity<String> reject(String id, String reason, Principal principal);

    ResponseEntity<String> pushBack(String id, String reason, Principal principal);

    ResponseEntity<String> getAllPendingDrivers();

    ResponseEntity<String> getAllApprovedDrivers();

    ResponseEntity<String> getAllRejectedDrivers();

    ResponseEntity<String> getAllPushedBackDrivers();

    Driver findByEmail(String email);

    Driver findByIdNumber(String idNumber);

    Driver findByLicenseNumber(String licenseNumber);

    List<Driver> findByCompanyName(String companyName);
}
