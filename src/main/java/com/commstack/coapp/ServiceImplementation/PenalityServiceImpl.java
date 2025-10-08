package com.commstack.coapp.ServiceImplementation;

import com.commstack.coapp.Models.Penality;
import com.commstack.coapp.Models.UserAuditTrail;
import com.commstack.coapp.Repositories.PenalityRepository;
import com.commstack.coapp.Service.PenalityService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PenalityServiceImpl implements PenalityService {

    private final PenalityRepository penalityRepository;
    private final MongoTemplate mongoTemplate;

    public PenalityServiceImpl(PenalityRepository penalityRepository, MongoTemplate mongoTemplate) {
        this.penalityRepository = penalityRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Penality create(Penality penality, Principal principal) {
        String user = principal != null ? principal.getName() : "system";
        penality.setCreatedBy(user);
        penality.setCreatedAt(LocalDateTime.now());
        penality.setUpdatedBy(user);
        penality.setUpdatedAt(LocalDateTime.now());
        penality.setStatus(penality.getStatus() == null ? "Outstanding" : penality.getStatus());
        Penality saved = penalityRepository.save(penality);

        // create audit trail
        try {
            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(saved.getId())
                    .action("CREATE_PENALITY")
                    .description("Created penality for shaft " + saved.getShaftNumber())
                    .doneBy(user)
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "penality_audit_trail");
        } catch (Exception e) {
            // don't fail the create if audit write fails
            System.err.println("Failed to write penality audit: " + e.getMessage());
        }

        return saved;
    }

    @Override
    public List<Penality> getAll() {
        return penalityRepository.findAll();
    }

    @Override
    public Penality getById(String id) {
        Optional<Penality> opt = penalityRepository.findById(id);
        return opt.orElse(null);
    }

    @Override
    public Penality update(String id, Penality penality, Principal principal) {
        Optional<Penality> opt = penalityRepository.findById(id);
        if (!opt.isPresent()) return null;
        Penality existing = opt.get();
        // save original for audit
        Penality original = Penality.builder()
                .id(existing.getId())
                .shaftNumber(existing.getShaftNumber())
                .section(existing.getSection())
                .penilatyFee(existing.getPenilatyFee())
                .reportedBy(existing.getReportedBy())
                .issue(existing.getIssue())
                .status(existing.getStatus())
                .remarks(existing.getRemarks())
                .createdBy(existing.getCreatedBy())
                .createdAt(existing.getCreatedAt())
                .updatedBy(existing.getUpdatedBy())
                .updatedAt(existing.getUpdatedAt())
                .build();

        // update allowed fields
        existing.setShaftNumber(penality.getShaftNumber());
        existing.setSection(penality.getSection());
        existing.setPenilatyFee(penality.getPenilatyFee());
        existing.setReportedBy(penality.getReportedBy());
        existing.setIssue(penality.getIssue());
        existing.setStatus(penality.getStatus());
        existing.setRemarks(penality.getRemarks());
        existing.setUpdatedBy(principal != null ? principal.getName() : "system");
        existing.setUpdatedAt(LocalDateTime.now());
        Penality saved = penalityRepository.save(existing);

        // create audit trail with before/after
        try {
            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(saved.getId())
                    .action("UPDATE_PENALITY")
                    .description("Updated penality. before=" + original + ", after=" + saved)
                    .doneBy(saved.getUpdatedBy())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "penality_audit_trail");
        } catch (Exception e) {
            System.err.println("Failed to write penality update audit: " + e.getMessage());
        }

        return saved;
    }

    @Override
    public void delete(String id) {
        Optional<Penality> opt = penalityRepository.findById(id);
        if (opt.isPresent()) {
            Penality existing = opt.get();
            penalityRepository.deleteById(id);
            try {
                UserAuditTrail audit = UserAuditTrail.builder()
                        .userId(existing.getId())
                        .action("DELETE_PENALITY")
                        .description("Deleted penality for shaft " + existing.getShaftNumber())
                        .doneBy(existing.getUpdatedBy() != null ? existing.getUpdatedBy() : existing.getCreatedBy())
                        .dateTime(LocalDateTime.now())
                        .build();
                mongoTemplate.save(audit, "penality_audit_trail");
            } catch (Exception e) {
                System.err.println("Failed to write penality delete audit: " + e.getMessage());
            }
        }
    }

    @Override
    public List<Penality> findByShaftNumber(String shaftNumber) {
        return penalityRepository.findByShaftNumber(shaftNumber);
    }

    @Override
    public Penality markAsPaid(String id, Principal principal) {
        Optional<Penality> opt = penalityRepository.findById(id);
        if (!opt.isPresent()) return null;
        Penality existing = opt.get();
        String user = principal != null ? principal.getName() : "system";
        String oldStatus = existing.getStatus();
        existing.setStatus("Paid");
        existing.setUpdatedBy(user);
        existing.setUpdatedAt(LocalDateTime.now());
        Penality saved = penalityRepository.save(existing);

        try {
            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(saved.getId())
                    .action("MARK_PENALITY_PAID")
                    .description("Status changed from " + oldStatus + " to Paid for shaft " + saved.getShaftNumber())
                    .doneBy(user)
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "penality_audit_trail");
        } catch (Exception e) {
            System.err.println("Failed to write penality mark-as-paid audit: " + e.getMessage());
        }

        return saved;
    }
}
