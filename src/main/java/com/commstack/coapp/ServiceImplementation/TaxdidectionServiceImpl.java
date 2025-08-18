package com.commstack.coapp.ServiceImplementation;

import com.commstack.coapp.Repositories.TaxdidectionRepository;

import com.commstack.coapp.Models.UserAuditTrail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.commstack.coapp.Models.SHEActivity;
import com.commstack.coapp.Models.Taxdidection;
import com.commstack.coapp.Service.TaxdidectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.time.LocalDate;
import java.util.*;

@Service
public class TaxdidectionServiceImpl implements TaxdidectionService {

    @Autowired
    private TaxdidectionRepository taxdidectionRepository;

    public List<Taxdidection> getAllApproved() {
        List<Taxdidection> result = new ArrayList<>();
        for (Taxdidection t : taxdidectionRepository.findAll()) {
            if ("APPROVED".equalsIgnoreCase(t.getStatus())) {
                result.add(t);
            }
        }
        return result;
    }

    public List<Taxdidection> getAllRejected() {
        List<Taxdidection> result = new ArrayList<>();
        for (Taxdidection t : taxdidectionRepository.findAll()) {
            if ("REJECTED".equalsIgnoreCase(t.getStatus())) {
                result.add(t);
            }
        }
        return result;
    }

    public List<Taxdidection> getAllPushedBack() {
        List<Taxdidection> result = new ArrayList<>();
        for (Taxdidection t : taxdidectionRepository.findAll()) {
            if ("PUSHED_BACK".equalsIgnoreCase(t.getStatus())) {
                result.add(t);
            }
        }
        return result;
    }

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public ResponseEntity<Taxdidection> create(Taxdidection taxdidection, Principal principal) {
        String id = UUID.randomUUID().toString();
        taxdidection.setId(id);
        taxdidection.setStatus("PENDING");
        taxdidection.setReason("");

        Taxdidection saved = taxdidectionRepository.save(taxdidection);
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
        return taxdidectionRepository.findAll();
    }

    @Override
    public Taxdidection getById(String id) {
        return taxdidectionRepository.findById(id).orElse(null);
    }

    @Override
    public ResponseEntity<Taxdidection> update(String id, Taxdidection taxdidection, Principal principal) {
        if (!taxdidectionRepository.existsById(id)) {
            return ResponseEntity.status(404).body(null);
        }
        taxdidection.setId(id);
        Taxdidection updated = taxdidectionRepository.save(taxdidection);
        UserAuditTrail audit = UserAuditTrail.builder()
                .userId(id)
                .action("UPDATED")
                .description("Updated Taxdidection: " + taxdidection.getTaxType())
                .doneBy(principal != null ? principal.getName() : "system")
                .dateTime(java.time.LocalDateTime.now())
                .build();
        mongoTemplate.save(audit, "taxdidection_audit_trail");
        return ResponseEntity.ok(updated);
    }

    @Override
    public ResponseEntity<Taxdidection> delete(String id, Principal principal) {
        Optional<Taxdidection> removed = taxdidectionRepository.findById(id);
        if (removed.isPresent()) {
            taxdidectionRepository.deleteById(id);
            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("DELETED")
                    .description("Deleted Taxdidection with id: " + id)
                    .doneBy(principal != null ? principal.getName() : "system")
                    .dateTime(java.time.LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "taxdidection_audit_trail");
            return ResponseEntity.ok(removed.get());
        }
        return ResponseEntity.status(404).body(null);
    }

    @Override
    public ResponseEntity<Taxdidection> approve(String id, Principal principal) {
        Optional<Taxdidection> optionalTax = taxdidectionRepository.findById(id);
        if (!optionalTax.isPresent()) {
            return ResponseEntity.status(404).body(null);
        }
        Taxdidection tax = optionalTax.get();
        tax.setStatus("APPROVED");
        taxdidectionRepository.save(tax);
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
        Optional<Taxdidection> optionalTax = taxdidectionRepository.findById(id);
        if (!optionalTax.isPresent()) {
            return ResponseEntity.status(404).body(null);
        }
        Taxdidection tax = optionalTax.get();
        tax.setStatus("REJECTED");
        tax.setReason(reason);
        taxdidectionRepository.save(tax);
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
        Optional<Taxdidection> optionalTax = taxdidectionRepository.findById(id);
        if (!optionalTax.isPresent()) {
            return ResponseEntity.status(404).body(null);
        }
        Taxdidection tax = optionalTax.get();
        tax.setStatus("PUSHED_BACK");
        tax.setReason(reason);
        taxdidectionRepository.save(tax);
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
