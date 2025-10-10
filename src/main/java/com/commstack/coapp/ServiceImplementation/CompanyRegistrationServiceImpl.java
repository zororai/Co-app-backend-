package com.commstack.coapp.ServiceImplementation;

import com.commstack.coapp.Models.CompanyRegistration;
import com.commstack.coapp.Models.UserAuditTrail;
import com.commstack.coapp.Repositories.CompanyRegistrationRepository;
import com.commstack.coapp.Service.CompanyRegistrationService;
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
public class CompanyRegistrationServiceImpl implements CompanyRegistrationService {

    @Autowired
    private CompanyRegistrationRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private String generateRegistrationNumber() {
        String datePart = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
        int randomPart = (int) (Math.random() * 9000) + 1000;
        return "mines-" + datePart + "-" + randomPart;
    }

    @Override
    public ResponseEntity<String> create(CompanyRegistration company, Principal principal) {
        company.setCreatedBy(principal.getName());
        company.setCreatedAt(LocalDate.now());
        company.setShaftnumber(0);
        company.setRegistrationNumber(generateRegistrationNumber());
        company.setUpdatedBy(principal.getName());
        company.setUpdatedAt(LocalDate.now());
        company.setStatus("PENDING");

        CompanyRegistration savedCompany = repository.save(company);

        UserAuditTrail audit = UserAuditTrail.builder()
                .userId(savedCompany.getId())
                .action("CREATED")
                .description("New company registered: '" + company.getCompanyName() +
                        "', owner: '" + company.getOwnerName() + " " + company.getOwnerSurname() + "'")
                .doneBy(principal.getName())
                .dateTime(LocalDateTime.now())
                .build();
        mongoTemplate.save(audit, "company_audit_trail");

        return ResponseEntity.status(200).body("Company registered successfully");
    }

    @Override
    public List<CompanyRegistration> getAll() {
        return repository.findAll();
    }

    @Override
    public CompanyRegistration getById(String id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public ResponseEntity<String> update(String id, CompanyRegistration company, Principal principal) {
        Optional<CompanyRegistration> existing = repository.findById(id);
        if (existing.isPresent()) {
            CompanyRegistration existingCompany = existing.get();

            String originalStatus = existingCompany.getStatus();

            // Update fields
            existingCompany.setCompanyName(company.getCompanyName());
            existingCompany.setAddress(company.getAddress());
            existingCompany.setCellNumber(company.getCellNumber());
            existingCompany.setEmail(company.getEmail());
            existingCompany.setCompanyLogo(company.getCompanyLogo());
            existingCompany.setCertificateOfCooperation(company.getCertificateOfCooperation());
            existingCompany.setCr14Copy(company.getCr14Copy());
            existingCompany.setMiningCertificate(company.getMiningCertificate());
            existingCompany.setTaxClearance(company.getTaxClearance());
            existingCompany.setPassportPhoto(company.getPassportPhoto());
            existingCompany.setOwnerName(company.getOwnerName());
            existingCompany.setOwnerSurname(company.getOwnerSurname());
            existingCompany.setOwnerAddress(company.getOwnerAddress());
            existingCompany.setOwnerCellNumber(company.getOwnerCellNumber());
            existingCompany.setOwnerIdNumber(company.getOwnerIdNumber());
            existingCompany.setStatus(company.getStatus());
            existingCompany.setUpdatedBy(principal.getName());
            existingCompany.setUpdatedAt(LocalDate.now());

            repository.save(existingCompany);

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("UPDATED")
                    .description("Company updated. Status changed from '" + originalStatus + "' to '"
                            + company.getStatus() + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "company_audit_trail");

            return ResponseEntity.ok("Company updated successfully");
        }
        return ResponseEntity.status(404).body("Company not found");
    }

    @Override
    public ResponseEntity<String> delete(String id, Principal principal) {
        Optional<CompanyRegistration> companyToDelete = repository.findById(id);
        if (companyToDelete.isPresent()) {
            CompanyRegistration company = companyToDelete.get();

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("DELETED")
                    .description("Company deleted: '" + company.getCompanyName() + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "company_audit_trail");

            repository.deleteById(id);
            return ResponseEntity.ok("Company deleted successfully");
        }
        return ResponseEntity.status(404).body("Company not found");
    }

    @Override
    public ResponseEntity<String> approval(String id, Principal principal) {
        Optional<CompanyRegistration> existing = repository.findById(id);
        if (existing.isPresent()) {
            CompanyRegistration company = existing.get();

            if (!"PENDING".equals(company.getStatus())) {
                return ResponseEntity.status(400).body("Company is not in PENDING status");
            }

            String originalStatus = company.getStatus();
            company.setStatus("APPROVED");
            company.setUpdatedBy(principal.getName());
            company.setUpdatedAt(LocalDate.now());

            repository.save(company);

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("APPROVED")
                    .description("Company status changed from '" + originalStatus + "' to 'APPROVED' for: '" +
                            company.getCompanyName() + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "company_audit_trail");

            return ResponseEntity.ok("Company approved successfully");
        }
        return ResponseEntity.status(404).body("Company not found");
    }

    @Override
    public ResponseEntity<String> reject(String id, String reason, Principal principal) {
        Optional<CompanyRegistration> existing = repository.findById(id);
        if (existing.isPresent()) {
            CompanyRegistration company = existing.get();

            String originalStatus = company.getStatus();
            company.setStatus("REJECTED");
            company.setReason(reason);
            company.setUpdatedBy(principal.getName());
            company.setUpdatedAt(LocalDate.now());

            repository.save(company);

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("REJECTED")
                    .description(
                            "Company rejected. Status changed from '" + originalStatus + "' to 'REJECTED'. Reason: '" +
                                    reason + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "company_audit_trail");

            return ResponseEntity.ok("Company rejected successfully");
        }
        return ResponseEntity.status(404).body("Company not found");
    }

    @Override
    public ResponseEntity<String> pushBack(String id, String reason, Principal principal) {
        Optional<CompanyRegistration> existing = repository.findById(id);
        if (existing.isPresent()) {
            CompanyRegistration company = existing.get();

            String originalStatus = company.getStatus();
            company.setStatus("PUSHED_BACK");
            company.setReason(reason);
            company.setUpdatedBy(principal.getName());
            company.setUpdatedAt(LocalDate.now());

            repository.save(company);

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("PUSHED_BACK")
                    .description("Company pushed back. Status changed from '" + originalStatus +
                            "' to 'PUSHED_BACK'. Reason: '" + reason + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "company_audit_trail");

            return ResponseEntity.ok("Company pushed back successfully");
        }
        return ResponseEntity.status(404).body("Company not found");
    }

    @Override
    public ResponseEntity<List<CompanyRegistration>> getAllPendingCompanies() {
        List<CompanyRegistration> companies = repository.findByStatus("PENDING");
        return ResponseEntity.ok(companies);
    }

    @Override
    public ResponseEntity<List<CompanyRegistration>> getAllApprovedCompanies() {
        List<CompanyRegistration> companies = repository.findByStatus("APPROVED");
        return ResponseEntity.ok(companies);
    }

    @Override
    public ResponseEntity<List<CompanyRegistration>> getAllRejectedCompanies() {
        List<CompanyRegistration> companies = repository.findByStatus("REJECTED");
        return ResponseEntity.ok(companies);
    }

    @Override
    public ResponseEntity<List<CompanyRegistration>> getAllPushedBackCompanies() {
        List<CompanyRegistration> companies = repository.findByStatus("PUSHED_BACK");
        return ResponseEntity.ok(companies);
    }
}
