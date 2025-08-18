// Set activeStatus to false (deactivate)

package com.commstack.coapp.ServiceImplementation;

import com.commstack.coapp.Models.MillOnboarding;
import com.commstack.coapp.Repositories.MillOnboardingRepository;
import com.commstack.coapp.Service.MillOnboardingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.commstack.coapp.Models.UserAuditTrail;
import org.springframework.data.mongodb.core.MongoTemplate;

@Service
public class MillOnboardingServiceImpl implements MillOnboardingService {

    private final MillOnboardingRepository repository;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public MillOnboardingServiceImpl(MillOnboardingRepository repository, MongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
    }

    private String generateRegistrationNumber() {
        String datePart = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
        int randomPart = (int) (Math.random() * 9000) + 1000;
        return "MLL-" + datePart + "-" + randomPart;
    }

    @Override
    public ResponseEntity<MillOnboarding> create(MillOnboarding millOnboarding, Principal principal) {
        millOnboarding.setId(null); // Let MongoDB generate the id
        // Optionally set createdBy, createdDate if you add those fields
        millOnboarding.setMillId(generateRegistrationNumber());
        millOnboarding.setCreatedBy(principal.getName());
        millOnboarding.setStatus("PENDING");
        millOnboarding.setActiveStatus(true); // Assuming you want to set this to true by default
        millOnboarding.setStatusHealth("HEALTHY");
        millOnboarding.setCreatedDate(LocalDateTime.now().toString());
        millOnboarding.setUpdatedBy(principal.getName());
        millOnboarding.setUpdatedDate(LocalDateTime.now().toString());
        MillOnboarding saved = repository.save(millOnboarding);
        UserAuditTrail audit = UserAuditTrail.builder()
                .userId(saved.getId())
                .action("CREATED")
                .description("Created MillOnboarding: " + saved.getMillId())
                .doneBy(principal != null ? principal.getName() : "system")
                .dateTime(LocalDateTime.now())
                .build();
        mongoTemplate.save(audit, "user_audit_trail");
        return ResponseEntity.ok(saved);
    }

    public ResponseEntity<MillOnboarding> deactivate(String id, Principal principal) {
        Optional<MillOnboarding> existing = repository.findById(id);
        if (existing.isPresent()) {
            MillOnboarding mill = existing.get();
            mill.setActiveStatus(false);
            repository.save(mill);
            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("DEACTIVATED")
                    .description("Deactivated MillOnboarding: " + mill.getMillId())
                    .doneBy(principal != null ? principal.getName() : "system")
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "mill_onboarding_audit_trail");
            return ResponseEntity.ok(mill);
        }
        return ResponseEntity.notFound().build();
    }

    // Set activeStatus to true and status to APPROVED (activate)
    public ResponseEntity<MillOnboarding> activate(String id, Principal principal) {
        Optional<MillOnboarding> existing = repository.findById(id);
        if (existing.isPresent()) {
            MillOnboarding mill = existing.get();
            mill.setActiveStatus(true);
            mill.setStatus("APPROVED");
            repository.save(mill);
            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("ACTIVATED")
                    .description("Activated MillOnboarding: " + mill.getMillId())
                    .doneBy(principal != null ? principal.getName() : "system")
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "mill_onboarding_audit_trail");
            return ResponseEntity.ok(mill);
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    public List<MillOnboarding> getAll() {
        return repository.findAll();
    }

    @Override
    public MillOnboarding getById(String id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public ResponseEntity<MillOnboarding> update(String id, MillOnboarding millOnboarding, Principal principal) {
        Optional<MillOnboarding> existing = repository.findById(id);
        if (existing.isPresent()) {
            millOnboarding.setId(id);
            MillOnboarding saved = repository.save(millOnboarding);
            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("UPDATED")
                    .description("Updated MillOnboarding: " + saved.getMillId())
                    .doneBy(principal != null ? principal.getName() : "system")
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "mill_onboarding_audit_trail");
            return ResponseEntity.ok(saved);
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<String> delete(String id, Principal principal) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("DELETED")
                    .description("Deleted MillOnboarding with id: " + id)
                    .doneBy(principal != null ? principal.getName() : "system")
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "mill_onboarding_audit_trail");
            return ResponseEntity.ok("Deleted successfully");
        }
        return ResponseEntity.status(404).body("Not found");
    }

    @Override
    public ResponseEntity<MillOnboarding> approve(String id, Principal principal) {
        Optional<MillOnboarding> existing = repository.findById(id);
        if (existing.isPresent()) {
            MillOnboarding mill = existing.get();
            // Add a status field to MillOnboarding if you want to track status
            mill.setStatus("APPROVED");
            repository.save(mill);
            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("APPROVED")
                    .description("Approved MillOnboarding: " + mill.getMillId())
                    .doneBy(principal != null ? principal.getName() : "system")
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "mill_onboarding_audit_trail");
            return ResponseEntity.ok(mill);
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<MillOnboarding> reject(String id, String reason, Principal principal) {
        Optional<MillOnboarding> existing = repository.findById(id);
        if (existing.isPresent()) {
            MillOnboarding mill = existing.get();
            mill.setStatus("REJECTED");
            mill.setReason(reason);
            repository.save(mill);
            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("REJECTED")
                    .description("Rejected MillOnboarding: " + mill.getMillId() + ". Reason: " + reason)
                    .doneBy(principal != null ? principal.getName() : "system")
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "mill_onboarding_audit_trail");
            return ResponseEntity.ok(mill);
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<MillOnboarding> pushBack(String id, String reason, Principal principal) {
        Optional<MillOnboarding> existing = repository.findById(id);
        if (existing.isPresent()) {
            MillOnboarding mill = existing.get();
            mill.setStatus("PUSHED_BACK");
            mill.setReason(reason);
            repository.save(mill);
            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("PUSHED_BACK")
                    .description("Pushed back MillOnboarding: " + mill.getMillId() + ". Reason: " + reason)
                    .doneBy(principal != null ? principal.getName() : "system")
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "mill_onboarding_audit_trail");
            return ResponseEntity.ok(mill);
        }
        return ResponseEntity.notFound().build();
    }
}
