package com.commstack.coapp.ServiceImplementation;

import com.commstack.coapp.Models.Driver;
import com.commstack.coapp.Models.UserAuditTrail;
import com.commstack.coapp.Repositories.DriverRepository;
import com.commstack.coapp.Service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DriverServiceImpl implements DriverService {

    @Autowired
    private DriverRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public ResponseEntity<String> create(Driver driver, Principal principal) {
        // Check if driver with same ID number already exists
        if (repository.findByIdNumber(driver.getIdNumber()) != null) {
            return ResponseEntity.badRequest().body("Driver with this ID number already exists");
        }

        // Check if driver with same email already exists
        if (repository.findByEmail(driver.getEmail()) != null) {
            return ResponseEntity.badRequest().body("Driver with this email already exists");
        }

        // Check if driver with same license number already exists
        if (repository.findByLicenseNumber(driver.getLicenseNumber()) != null) {
            return ResponseEntity.badRequest().body("Driver with this license number already exists");
        }

        driver.setCreatedBy(principal.getName());
        driver.setCreatedAt(LocalDate.now());
        driver.setUpdatedBy(principal.getName());
        driver.setUpdatedAt(LocalDate.now());
        driver.setStatus("PENDING");

        Driver savedDriver = repository.save(driver);

        UserAuditTrail audit = UserAuditTrail.builder()
                .userId(savedDriver.getId())
                .action("CREATED")
                .description("New driver registered: '" + driver.getName() + " " + driver.getSurname() +
                        "', License Number: '" + driver.getLicenseNumber() + "'")
                .doneBy(principal.getName())
                .dateTime(LocalDateTime.now())
                .build();
        mongoTemplate.save(audit, "driver_audit_trail");

        return ResponseEntity.ok("Driver registered successfully");
    }

    @Override
    public List<Driver> getAll() {
        return repository.findAll();
    }

    @Override
    public Driver getById(String id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Driver findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public Driver findByIdNumber(String idNumber) {
        return repository.findByIdNumber(idNumber);
    }

    @Override
    public Driver findByLicenseNumber(String licenseNumber) {
        return repository.findByLicenseNumber(licenseNumber);
    }

    @Override
    public List<Driver> findByCompanyName(String companyName) {
        return repository.findByCompanyName(companyName);
    }

    @Override
    public ResponseEntity<String> update(String id, Driver driver, Principal principal) {
        Optional<Driver> existing = repository.findById(id);
        if (existing.isPresent()) {
            Driver existingDriver = existing.get();

            // Check if email is being changed and new email already exists
            if (!existingDriver.getEmail().equals(driver.getEmail()) &&
                    repository.findByEmail(driver.getEmail()) != null) {
                return ResponseEntity.badRequest().body("Driver with this email already exists");
            }

            // Check if ID number is being changed and new ID number already exists
            if (!existingDriver.getIdNumber().equals(driver.getIdNumber()) &&
                    repository.findByIdNumber(driver.getIdNumber()) != null) {
                return ResponseEntity.badRequest().body("Driver with this ID number already exists");
            }

            // Check if license number is being changed and new license number already
            // exists
            if (!existingDriver.getLicenseNumber().equals(driver.getLicenseNumber()) &&
                    repository.findByLicenseNumber(driver.getLicenseNumber()) != null) {
                return ResponseEntity.badRequest().body("Driver with this license number already exists");
            }

            String originalStatus = existingDriver.getStatus();

            // Update fields
            existingDriver.setName(driver.getName());
            existingDriver.setSurname(driver.getSurname());
            existingDriver.setIdNumber(driver.getIdNumber());
            existingDriver.setAddress(driver.getAddress());
            existingDriver.setCellNumber(driver.getCellNumber());
            existingDriver.setEmail(driver.getEmail());
            existingDriver.setCompanyName(driver.getCompanyName());
            existingDriver.setLicenseNumber(driver.getLicenseNumber());
            existingDriver.setPosition(driver.getPosition());
            existingDriver.setStartContractDate(driver.getStartContractDate());
            existingDriver.setDriverLicense(driver.getDriverLicense());
            existingDriver.setIdCopy(driver.getIdCopy());
            existingDriver.setMedicalCertificate(driver.getMedicalCertificate());
            existingDriver.setDefensiveDrivingCertificate(driver.getDefensiveDrivingCertificate());
            existingDriver.setProfilePhoto(driver.getProfilePhoto());
            existingDriver.setStatus(driver.getStatus());
            existingDriver.setUpdatedBy(principal.getName());
            existingDriver.setUpdatedAt(LocalDate.now());

            repository.save(existingDriver);

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("UPDATED")
                    .description("Driver updated. Status changed from '" + originalStatus + "' to '" +
                            driver.getStatus() + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "driver_audit_trail");

            return ResponseEntity.ok("Driver updated successfully");
        }
        return ResponseEntity.status(404).body("Driver not found");
    }

    @Override
    public ResponseEntity<String> delete(String id, Principal principal) {
        Optional<Driver> driverToDelete = repository.findById(id);
        if (driverToDelete.isPresent()) {
            Driver driver = driverToDelete.get();

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("DELETED")
                    .description("Driver deleted: '" + driver.getName() + " " + driver.getSurname() +
                            "', License Number: '" + driver.getLicenseNumber() + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "driver_audit_trail");

            repository.deleteById(id);
            return ResponseEntity.ok("Driver deleted successfully");
        }
        return ResponseEntity.status(404).body("Driver not found");
    }

    @Override
    public ResponseEntity<String> approval(String id, Principal principal) {
        Optional<Driver> existing = repository.findById(id);
        if (existing.isPresent()) {
            Driver driver = existing.get();

            if (!"PENDING".equals(driver.getStatus())) {
                return ResponseEntity.status(400).body("Driver is not in PENDING status");
            }

            String originalStatus = driver.getStatus();
            driver.setStatus("APPROVED");
            driver.setUpdatedBy(principal.getName());
            driver.setUpdatedAt(LocalDate.now());

            repository.save(driver);

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("APPROVED")
                    .description("Driver status changed from '" + originalStatus + "' to 'APPROVED' for: '" +
                            driver.getName() + " " + driver.getSurname() + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "driver_audit_trail");

            return ResponseEntity.ok("Driver approved successfully");
        }
        return ResponseEntity.status(404).body("Driver not found");
    }

    @Override
    public ResponseEntity<String> reject(String id, String reason, Principal principal) {
        Optional<Driver> existing = repository.findById(id);
        if (existing.isPresent()) {
            Driver driver = existing.get();

            String originalStatus = driver.getStatus();
            driver.setStatus("REJECTED");
            driver.setReason(reason);
            driver.setUpdatedBy(principal.getName());
            driver.setUpdatedAt(LocalDate.now());

            repository.save(driver);

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("REJECTED")
                    .description("Driver rejected. Status changed from '" + originalStatus +
                            "' to 'REJECTED'. Reason: '" + reason + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "driver_audit_trail");

            return ResponseEntity.ok("Driver rejected successfully");
        }
        return ResponseEntity.status(404).body("Driver not found");
    }

    @Override
    public ResponseEntity<String> pushBack(String id, String reason, Principal principal) {
        Optional<Driver> existing = repository.findById(id);
        if (existing.isPresent()) {
            Driver driver = existing.get();

            String originalStatus = driver.getStatus();
            driver.setStatus("PUSHED_BACK");
            driver.setReason(reason);
            driver.setUpdatedBy(principal.getName());
            driver.setUpdatedAt(LocalDate.now());

            repository.save(driver);

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("PUSHED_BACK")
                    .description("Driver pushed back. Status changed from '" + originalStatus +
                            "' to 'PUSHED_BACK'. Reason: '" + reason + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "driver_audit_trail");

            return ResponseEntity.ok("Driver pushed back successfully");
        }
        return ResponseEntity.status(404).body("Driver not found");
    }

    @Override
    public ResponseEntity<String> getAllPendingDrivers() {
        List<Driver> drivers = repository.findByStatus("PENDING");
        return ResponseEntity.ok(drivers.toString());
    }

    @Override
    public ResponseEntity<String> getAllApprovedDrivers() {
        List<Driver> drivers = repository.findByStatus("APPROVED");
        return ResponseEntity.ok(drivers.toString());
    }

    @Override
    public ResponseEntity<String> getAllRejectedDrivers() {
        List<Driver> drivers = repository.findByStatus("REJECTED");
        return ResponseEntity.ok(drivers.toString());
    }

    @Override
    public ResponseEntity<String> getAllPushedBackDrivers() {
        List<Driver> drivers = repository.findByStatus("PUSHED_BACK");
        return ResponseEntity.ok(drivers.toString());
    }
}
