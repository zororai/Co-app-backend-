package com.commstack.coapp.ServiceImplementation;

import com.commstack.coapp.Models.UserAuditTrail;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.commstack.coapp.Models.Taxdidection;
import com.commstack.coapp.Service.TaxdidectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.util.*;

@Service
public class TaxdidectionServiceImpl implements TaxdidectionService {
    private MongoTemplate mongoTemplate;
    private final Map<String, Taxdidection> store = new HashMap<>();

    @Override
    public ResponseEntity<Taxdidection> create(Taxdidection taxdidection, Principal principal) {
        String id = UUID.randomUUID().toString();
        taxdidection.setId(id);
        taxdidection.setStatus("PENDING");
        store.put(id, taxdidection);
        UserAuditTrail audit = UserAuditTrail.builder()
                .userId(id)
                .action("CREATED")
                .description("Created Taxdidection: " + taxdidection.getTaxType())
                .doneBy(principal != null ? principal.getName() : "system")
                .dateTime(java.time.LocalDateTime.now())
                .build();
        mongoTemplate.save(audit, "taxdidection_audit_trail");
        return ResponseEntity.ok(taxdidection);
    }

    @Override
    public List<Taxdidection> getAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Taxdidection getById(String id) {
        return store.get(id);
    }

    @Override
    public ResponseEntity<Taxdidection> update(String id, Taxdidection taxdidection, Principal principal) {
        if (!store.containsKey(id)) {
            return ResponseEntity.status(404).body(null);
        }
        taxdidection.setId(id);
        store.put(id, taxdidection);
        UserAuditTrail audit = UserAuditTrail.builder()
                .userId(id)
                .action("UPDATED")
                .description("Updated Taxdidection: " + taxdidection.getTaxType())
                .doneBy(principal != null ? principal.getName() : "system")
                .dateTime(java.time.LocalDateTime.now())
                .build();
        mongoTemplate.save(audit, "taxdidection_audit_trail");
        return ResponseEntity.ok(taxdidection);
    }

    @Override
    public ResponseEntity<Taxdidection> delete(String id, Principal principal) {
        Taxdidection removed = store.remove(id);
        if (removed != null) {
            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("DELETED")
                    .description("Deleted Taxdidection with id: " + id)
                    .doneBy(principal != null ? principal.getName() : "system")
                    .dateTime(java.time.LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "taxdidection_audit_trail");
            return ResponseEntity.ok(removed);
        }
        return ResponseEntity.status(404).body(null);
    }

    @Override
    public ResponseEntity<Taxdidection> approve(String id, Principal principal) {
        Taxdidection tax = store.get(id);
        if (tax == null) {
            return ResponseEntity.status(404).body(null);
        }
        tax.setStatus("APPROVED");
        UserAuditTrail audit = UserAuditTrail.builder()
                .userId(id)
                .action("APPROVED")
                .description("Approved Taxdidection: " + tax.getTaxType())
                .doneBy(principal != null ? principal.getName() : "system")
                .dateTime(java.time.LocalDateTime.now())
                .build();
        mongoTemplate.save(audit, "taxdidection_audit_trail");
        return ResponseEntity.ok(tax);
    }

    @Override
    public ResponseEntity<Taxdidection> reject(String id, String reason, Principal principal) {
        Taxdidection tax = store.get(id);
        if (tax == null) {
            return ResponseEntity.status(404).body(null);
        }
        tax.setStatus("REJECTED");
        tax.setReason(reason);
        UserAuditTrail audit = UserAuditTrail.builder()
                .userId(id)
                .action("REJECTED")
                .description("Rejected Taxdidection: " + tax.getTaxType() + ". Reason: " + reason)
                .doneBy(principal != null ? principal.getName() : "system")
                .dateTime(java.time.LocalDateTime.now())
                .build();
        mongoTemplate.save(audit, "taxdidection_audit_trail");
        return ResponseEntity.ok(tax);
    }

    @Override
    public ResponseEntity<Taxdidection> pushBack(String id, String reason, Principal principal) {
        Taxdidection tax = store.get(id);
        if (tax == null) {
            return ResponseEntity.status(404).body(null);
        }
        tax.setStatus("PUSHED_BACK");
        tax.setReason(reason);
        UserAuditTrail audit = UserAuditTrail.builder()
                .userId(id)
                .action("PUSHED_BACK")
                .description("Pushed back Taxdidection: " + tax.getTaxType() + ". Reason: " + reason)
                .doneBy(principal != null ? principal.getName() : "system")
                .dateTime(java.time.LocalDateTime.now())
                .build();
        mongoTemplate.save(audit, "taxdidection_audit_trail");
        return ResponseEntity.ok(tax);
    }
}
