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
    public ResponseEntity<SecurityCompany> create(SecurityCompany company, Principal principal) {
        // Check if company with same contact email already exists
        if (repository.findByContactEmail(company.getContactEmail()) != null) {
            return ResponseEntity.badRequest().body(null);
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
                        "', Registration Number: '" + company.getRegistrationNumber() + "'")
                .doneBy(principal.getName())
                .dateTime(LocalDateTime.now())
                .build();
        mongoTemplate.save(audit, "security_company_audit_trail");

        return ResponseEntity.ok(savedCompany);
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
        return repository.findByContactEmail(emailAddress);
    }

    @Override
    public SecurityCompany findByBpNumber(String bpNumber) {
        // No bpNumber in new model, method can be removed or return null
        return null;
    }

    @Override
    public ResponseEntity<String> update(String id, SecurityCompany company, Principal principal) {
        Optional<SecurityCompany> existing = repository.findById(id);
        if (existing.isPresent()) {
            SecurityCompany existingCompany = existing.get();

            // Check if contact email is being changed and new email already exists
            if (!existingCompany.getContactEmail().equals(company.getContactEmail()) &&
                    repository.findByContactEmail(company.getContactEmail()) != null) {
                return ResponseEntity.badRequest().body("Company with this contact email already exists");
            }

            // Check if registration number is being changed and new registration number
            // already exists
            if (!existingCompany.getRegistrationNumber().equals(company.getRegistrationNumber()) &&
                    repository.findByRegistrationNumber(company.getRegistrationNumber()) != null) {
                return ResponseEntity.badRequest().body("Company with this registration number already exists");
            }

            String originalStatus = existingCompany.getStatus();

            // Update fields
            existingCompany.setCompanyName(company.getCompanyName());
            existingCompany.setRegistrationNumber(company.getRegistrationNumber());
            existingCompany.setContactPhone(company.getContactPhone());
            existingCompany.setContactPersonName(company.getContactPersonName());
            existingCompany.setContactEmail(company.getContactEmail());
            existingCompany.setSiteAddress(company.getSiteAddress());
            existingCompany.setServiceType(company.getServiceType());
            existingCompany.setHeadOfficeAddress(company.getHeadOfficeAddress());
            existingCompany.setNumberOfWorks(company.getNumberOfWorks());
            existingCompany.setStartContractDate(company.getStartContractDate());
            existingCompany.setEndContractDate(company.getEndContractDate());
            existingCompany.setEmergencyContactPhone(company.getEmergencyContactPhone());
            existingCompany.setEmergencyContactName(company.getEmergencyContactName());
            existingCompany.setLocations(company.getLocations());
            existingCompany.setValidTaxClearance(company.getValidTaxClearance());
            existingCompany.setCompanyLogo(company.getCompanyLogo());
            existingCompany.setGetCertificateOfCooperation(company.getGetCertificateOfCooperation());
            existingCompany.setOperatingLicense(company.getOperatingLicense());
            existingCompany.setProofOfInsurance(company.getProofOfInsurance());
            existingCompany.setSiteRiskAssessmentReport(company.getSiteRiskAssessmentReport());
            existingCompany.setStatus(company.getStatus());
            existingCompany.setReason(company.getReason());
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
                            "', Registration Number: '" + company.getRegistrationNumber() + "'")
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

    @Override
    public long getApprovedCompanyCount() {
        return repository.countByStatusIgnoreCase("APPROVED");
    }
}
