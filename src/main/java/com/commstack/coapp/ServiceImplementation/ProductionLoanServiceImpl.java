package com.commstack.coapp.ServiceImplementation;

import com.commstack.coapp.Models.ProductionLoan;
import com.commstack.coapp.Repositories.ProductionLoanRepository;
import com.commstack.coapp.Service.ProductionLoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import com.commstack.coapp.Models.UserAuditTrail;
import org.springframework.data.mongodb.core.MongoTemplate;

@Service
public class ProductionLoanServiceImpl implements ProductionLoanService {

    public List<ProductionLoan> getAllApproved() {
        return repository.findAll().stream()
                .filter(loan -> "APPROVED".equalsIgnoreCase(loan.getStatus()))
                .toList();
    }

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ProductionLoanRepository repository;

    @Override
    public ResponseEntity<ProductionLoan> create(ProductionLoan productionLoan, Principal principal) {
        productionLoan.setId(null); // Let MongoDB generate the id
        productionLoan.setStatus("PENDING"); // Set default status to PENDING
        productionLoan.setReason("Waiting for approval");

        ProductionLoan saved = repository.save(productionLoan);

        UserAuditTrail audit = UserAuditTrail.builder()
                .userId(saved.getId())
                .action("CREATED")
                .description("Created ProductionLoan: " + saved.getLoanName())
                .doneBy(principal != null ? principal.getName() : "system")
                .dateTime(java.time.LocalDateTime.now())
                .build();
        mongoTemplate.save(audit, "production_loan_audit_trail");
        return ResponseEntity.ok(saved);
    }

    @Override
    public List<ProductionLoan> getAll() {
        return repository.findAll();
    }

    @Override
    public ResponseEntity<ProductionLoan> approve(String id, Principal principal) {
        Optional<ProductionLoan> existing = repository.findById(id);
        if (existing.isPresent()) {
            ProductionLoan loan = existing.get();
            loan.setStatus("APPROVED");
            ProductionLoan saved = repository.save(loan);
            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("APPROVED")
                    .description("Approved ProductionLoan: " + saved.getLoanName())
                    .doneBy(principal != null ? principal.getName() : "system")
                    .dateTime(java.time.LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "production_loan_audit_trail");
            return ResponseEntity.ok(saved);
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<ProductionLoan> reject(String id, String reason, Principal principal) {
        Optional<ProductionLoan> existing = repository.findById(id);
        if (existing.isPresent()) {
            ProductionLoan loan = existing.get();
            loan.setStatus("REJECTED");
            loan.setReason(reason);
            ProductionLoan saved = repository.save(loan);
            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("REJECTED")
                    .description("Rejected ProductionLoan: " + saved.getLoanName() + ". Reason: " + reason)
                    .doneBy(principal != null ? principal.getName() : "system")
                    .dateTime(java.time.LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "production_loan_audit_trail");
            return ResponseEntity.ok(saved);
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<ProductionLoan> pushBack(String id, String reason, Principal principal) {
        Optional<ProductionLoan> existing = repository.findById(id);
        if (existing.isPresent()) {
            ProductionLoan loan = existing.get();
            loan.setStatus("PUSHED_BACK");
            loan.setReason(reason);
            ProductionLoan saved = repository.save(loan);
            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("PUSHED_BACK")
                    .description("Pushed back ProductionLoan: " + saved.getLoanName() + ". Reason: " + reason)
                    .doneBy(principal != null ? principal.getName() : "system")
                    .dateTime(java.time.LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "production_loan_audit_trail");
            return ResponseEntity.ok(saved);
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    public ProductionLoan getById(String id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public ResponseEntity<ProductionLoan> update(String id, ProductionLoan productionLoan, Principal principal) {
        Optional<ProductionLoan> existing = repository.findById(id);
        if (existing.isPresent()) {
            productionLoan.setId(id);
            ProductionLoan saved = repository.save(productionLoan);
            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("UPDATED")
                    .description("Updated ProductionLoan: " + saved.getLoanName())
                    .doneBy(principal != null ? principal.getName() : "system")
                    .dateTime(java.time.LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "production_loan_audit_trail");
            return ResponseEntity.ok(saved);
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<String> delete(String id, Principal principal) {
        if (repository.existsById(id)) {
            Optional<ProductionLoan> existing = repository.findById(id);
            repository.deleteById(id);
            if (existing.isPresent()) {
                UserAuditTrail audit = UserAuditTrail.builder()
                        .userId(id)
                        .action("DELETED")
                        .description("Deleted ProductionLoan: " + existing.get().getLoanName())
                        .doneBy(principal != null ? principal.getName() : "system")
                        .dateTime(java.time.LocalDateTime.now())
                        .build();
                mongoTemplate.save(audit, "production_loan_audit_trail");
            }
            return ResponseEntity.ok("Deleted successfully");
        }
        return ResponseEntity.status(404).body("Not found");
    }
}
