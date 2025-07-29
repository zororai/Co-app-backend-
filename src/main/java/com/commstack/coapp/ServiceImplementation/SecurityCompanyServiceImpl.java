package com.commstack.coapp.ServiceImplementation;

import com.commstack.coapp.Models.SecurityCompany;
import com.commstack.coapp.Models.UserAuditTrail;
import com.commstack.coapp.Repositories.SecurityCompanyRepository;
import com.commstack.coapp.Service.SecurityCompanyService;
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
public class SecurityCompanyServiceImpl implements SecurityCompanyService {

    @Autowired
    private SecurityCompanyRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public ResponseEntity<String> create(SecurityCompany company, Principal principal) {
        // Check if company with same BP number already exists
        if (repository.findByBpNumber(company.getBpNumber()) != null) {
            return ResponseEntity.badRequest().body("Company with this BP number already exists");
        }

        // Check if company with same email already exists
        if (repository.findByEmailAddress(company.getEmailAddress()) != null) {
            return ResponseEntity.badRequest().body("Company with this email address already exists");
        }

        company.setCreatedBy(principal.getName());
        company.setCreatedAt(LocalDate.now());
        company.setUpdatedBy(principal.getName());
        company.setUpdatedAt(LocalDate.now());
        company.setStatus("PENDING");

        SecurityCompany savedCompany = repository.save(company);

        UserAuditTrail audit = UserAuditTrail.builder()
                .userId(savedCompany.getId())
                .action("CREATED")
                .description("New security company registered: '" + company.getCompanyName() +
                        "', BP Number: '" + company.getBpNumber() + "'")
                .doneBy(principal.getName())
                .dateTime(LocalDateTime.now())
                .build();
        mongoTemplate.save(audit, "security_company_audit_trail");

        return ResponseEntity.ok("Security company registered successfully");
    }

    @Override
    public List<SecurityCompany> getAll() {
        return repository.findAll();
    }

    @Override
    public SecurityCompany getById(String id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public SecurityCompany findByEmailAddress(String emailAddress) {
        return repository.findByEmailAddress(emailAddress);
    }

    @Override
    public SecurityCompany findByBpNumber(String bpNumber) {
        return repository.findByBpNumber(bpNumber);
    }

    @Override
    public ResponseEntity<String> update(String id, SecurityCompany company, Principal principal) {
        Optional<SecurityCompany> existing = repository.findById(id);
        if (existing.isPresent()) {
            SecurityCompany existingCompany = existing.get();

            // Check if email is being changed and new email already exists
            if (!existingCompany.getEmailAddress().equals(company.getEmailAddress()) &&
                    repository.findByEmailAddress(company.getEmailAddress()) != null) {
                return ResponseEntity.badRequest().body("Company with this email address already exists");
            }

            // Check if BP number is being changed and new BP number already exists
            if (!existingCompany.getBpNumber().equals(company.getBpNumber()) &&
                    repository.findByBpNumber(company.getBpNumber()) != null) {
                return ResponseEntity.badRequest().body("Company with this BP number already exists");
            }

            String originalStatus = existingCompany.getStatus();

            // Update fields
            existingCompany.setCompanyName(company.getCompanyName());
            existingCompany.setBpNumber(company.getBpNumber());
            existingCompany.setOwnerIdNumber(company.getOwnerIdNumber());
            existingCompany.setOwnerName(company.getOwnerName());
            existingCompany.setOwnerSurname(company.getOwnerSurname());
            existingCompany.setOwnerAddress(company.getOwnerAddress());
            existingCompany.setNumberOfEmployees(company.getNumberOfEmployees());
            existingCompany.setCompanyAddress(company.getCompanyAddress());
            existingCompany.setPosition(company.getPosition());
            existingCompany.setStartContractDate(company.getStartContractDate());
            existingCompany.setEmailAddress(company.getEmailAddress());
            existingCompany.setOwnerCellNumber(company.getOwnerCellNumber());
            existingCompany.setValidTaxClearance(company.getValidTaxClearance());
            existingCompany.setCompanyLogo(company.getCompanyLogo());
            existingCompany.setCr14Copy(company.getCr14Copy());
            existingCompany.setOperatingLicense(company.getOperatingLicense());
            existingCompany.setStatus(company.getStatus());
            existingCompany.setUpdatedBy(principal.getName());
            existingCompany.setUpdatedAt(LocalDate.now());

            repository.save(existingCompany);

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("UPDATED")
                    .description("Security company updated. Status changed from '" + originalStatus +
                            "' to '" + company.getStatus() + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "security_company_audit_trail");

            return ResponseEntity.ok("Security company updated successfully");
        }
        return ResponseEntity.status(404).body("Security company not found");
    }

    @Override
    public ResponseEntity<String> delete(String id, Principal principal) {
        Optional<SecurityCompany> companyToDelete = repository.findById(id);
        if (companyToDelete.isPresent()) {
            SecurityCompany company = companyToDelete.get();

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("DELETED")
                    .description("Security company deleted: '" + company.getCompanyName() +
                            "', BP Number: '" + company.getBpNumber() + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "security_company_audit_trail");

            repository.deleteById(id);
            return ResponseEntity.ok("Security company deleted successfully");
        }
        return ResponseEntity.status(404).body("Security company not found");
    }

    @Override
    public ResponseEntity<String> approval(String id, Principal principal) {
        Optional<SecurityCompany> existing = repository.findById(id);
        if (existing.isPresent()) {
            SecurityCompany company = existing.get();

            if (!"PENDING".equals(company.getStatus())) {
                return ResponseEntity.status(400).body("Security company is not in PENDING status");
            }

            String originalStatus = company.getStatus();
            company.setStatus("APPROVED");
            company.setUpdatedBy(principal.getName());
            company.setUpdatedAt(LocalDate.now());

            repository.save(company);

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("APPROVED")
                    .description("Security company status changed from '" + originalStatus +
                            "' to 'APPROVED' for: '" + company.getCompanyName() + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "security_company_audit_trail");

            return ResponseEntity.ok("Security company approved successfully");
        }
        return ResponseEntity.status(404).body("Security company not found");
    }

    @Override
    public ResponseEntity<String> reject(String id, String reason, Principal principal) {
        Optional<SecurityCompany> existing = repository.findById(id);
        if (existing.isPresent()) {
            SecurityCompany company = existing.get();

            String originalStatus = company.getStatus();
            company.setStatus("REJECTED");
            company.setReason(reason);
            company.setUpdatedBy(principal.getName());
            company.setUpdatedAt(LocalDate.now());

            repository.save(company);

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("REJECTED")
                    .description("Security company rejected. Status changed from '" + originalStatus +
                            "' to 'REJECTED'. Reason: '" + reason + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "security_company_audit_trail");

            return ResponseEntity.ok("Security company rejected successfully");
        }
        return ResponseEntity.status(404).body("Security company not found");
    }

    @Override
    public ResponseEntity<String> pushBack(String id, String reason, Principal principal) {
        Optional<SecurityCompany> existing = repository.findById(id);
        if (existing.isPresent()) {
            SecurityCompany company = existing.get();

            String originalStatus = company.getStatus();
            company.setStatus("PUSHED_BACK");
            company.setReason(reason);
            company.setUpdatedBy(principal.getName());
            company.setUpdatedAt(LocalDate.now());

            repository.save(company);

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("PUSHED_BACK")
                    .description("Security company pushed back. Status changed from '" + originalStatus +
                            "' to 'PUSHED_BACK'. Reason: '" + reason + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "security_company_audit_trail");

            return ResponseEntity.ok("Security company pushed back successfully");
        }
        return ResponseEntity.status(404).body("Security company not found");
    }

    @Override
    public ResponseEntity<String> getAllPendingCompanies() {
        List<SecurityCompany> companies = repository.findByStatus("PENDING");
        return ResponseEntity.ok(companies.toString());
    }

    @Override
    public ResponseEntity<String> getAllApprovedCompanies() {
        List<SecurityCompany> companies = repository.findByStatus("APPROVED");
        return ResponseEntity.ok(companies.toString());
    }

    @Override
    public ResponseEntity<String> getAllRejectedCompanies() {
        List<SecurityCompany> companies = repository.findByStatus("REJECTED");
        return ResponseEntity.ok(companies.toString());
    }

    @Override
    public ResponseEntity<String> getAllPushedBackCompanies() {
        List<SecurityCompany> companies = repository.findByStatus("PUSHED_BACK");
        return ResponseEntity.ok(companies.toString());
    }
}
