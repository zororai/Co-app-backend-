package com.commstack.coapp.ServiceImplementation;

import com.commstack.coapp.Models.Section;
import com.commstack.coapp.Models.UserAuditTrail;
import com.commstack.coapp.Repositories.SectionRepository;
import com.commstack.coapp.Service.SectionService;
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
public class SectionServiceImpl implements SectionService {

    @Autowired
    private SectionRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public ResponseEntity<String> create(Section section, Principal principal) {
        // Check if section with same name already exists for this miner
        if (repository.findBySectionNameAndMinerId(section.getSectionName(), section.getMinerId()) != null) {
            return ResponseEntity.badRequest().body("Section with this name already exists for this miner");
        }

        section.setCreatedBy(principal.getName());
        section.setCreatedAt(LocalDate.now());
        section.setUpdatedBy(principal.getName());
        section.setActive(false);
        section.setUpdatedAt(LocalDate.now());
        section.setStatus("PENDING");

        Section savedSection = repository.save(section);

        UserAuditTrail audit = UserAuditTrail.builder()
                .userId(savedSection.getId())
                .action("CREATED")
                .description("New section created: '" + section.getSectionName() +
                        "', Number of Shafts: " + section.getNumberOfShaft())
                .doneBy(principal.getName())
                .dateTime(LocalDateTime.now())
                .build();
        mongoTemplate.save(audit, "section_audit_trail");

        return ResponseEntity.ok("Section created successfully");
    }

    @Override
    public List<Section> getAll() {
        return repository.findAll();
    }

    @Override
    public Section getById(String id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<Section> findByMinerId(String minerId) {
        return repository.findByMinerId(minerId);
    }

    @Override
    public Section findBySectionNameAndMinerId(String sectionName, String minerId) {
        return repository.findBySectionNameAndMinerId(sectionName, minerId);
    }

    @Override
    public List<Section> findByNumberOfShaft(String numberOfShaft) {
        return repository.findByNumberOfShaft(numberOfShaft);
    }

    @Override
    public ResponseEntity<String> update(String id, Section section, Principal principal) {
        Optional<Section> existing = repository.findById(id);
        if (existing.isPresent()) {
            Section existingSection = existing.get();

            // Check if section name is being changed and new name already exists for this
            // miner
            if (!existingSection.getSectionName().equals(section.getSectionName()) &&
                    repository.findBySectionNameAndMinerId(section.getSectionName(), section.getMinerId()) != null) {
                return ResponseEntity.badRequest().body("Section with this name already exists for this miner");
            }

            String originalStatus = existingSection.getStatus();

            // Update fields
            existingSection.setSectionName(section.getSectionName());
            existingSection.setNumberOfShaft(section.getNumberOfShaft());
            existingSection.setStatus(section.getStatus());
            existingSection.setUpdatedBy(principal.getName());
            existingSection.setUpdatedAt(LocalDate.now());

            repository.save(existingSection);

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("UPDATED")
                    .description("Section updated. Status changed from '" + originalStatus +
                            "' to '" + section.getStatus() + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "section_audit_trail");

            return ResponseEntity.ok("Section updated successfully");
        }
        return ResponseEntity.status(404).body("Section not found");
    }

    @Override
    public ResponseEntity<String> delete(String id, Principal principal) {
        Optional<Section> sectionToDelete = repository.findById(id);
        if (sectionToDelete.isPresent()) {
            Section section = sectionToDelete.get();

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("DELETED")
                    .description("Section deleted: '" + section.getSectionName() +
                            "', Number of Shafts: " + section.getNumberOfShaft())
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "section_audit_trail");

            repository.deleteById(id);
            return ResponseEntity.ok("Section deleted successfully");
        }
        return ResponseEntity.status(404).body("Section not found");
    }

    @Override
    public ResponseEntity<String> approval(String id, Principal principal) {
        Optional<Section> existing = repository.findById(id);
        if (existing.isPresent()) {
            Section section = existing.get();

            if (!"PENDING".equals(section.getStatus())) {
                return ResponseEntity.status(400).body("Section is not in PENDING status");
            }

            String originalStatus = section.getStatus();
            section.setStatus("APPROVED");
            section.setUpdatedBy(principal.getName());
            section.setUpdatedAt(LocalDate.now());

            repository.save(section);

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("APPROVED")
                    .description("Section status changed from '" + originalStatus +
                            "' to 'APPROVED' for: '" + section.getSectionName() + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "section_audit_trail");

            return ResponseEntity.ok("Section approved successfully");
        }
        return ResponseEntity.status(404).body("Section not found");
    }

    @Override
    public ResponseEntity<String> reject(String id, String reason, Principal principal) {
        Optional<Section> existing = repository.findById(id);
        if (existing.isPresent()) {
            Section section = existing.get();

            String originalStatus = section.getStatus();
            section.setStatus("REJECTED");
            section.setReason(reason);
            section.setUpdatedBy(principal.getName());
            section.setUpdatedAt(LocalDate.now());

            repository.save(section);

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("REJECTED")
                    .description("Section rejected. Status changed from '" + originalStatus +
                            "' to 'REJECTED'. Reason: '" + reason + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "section_audit_trail");

            return ResponseEntity.ok("Section rejected successfully");
        }
        return ResponseEntity.status(404).body("Section not found");
    }

    @Override
    public ResponseEntity<String> pushBack(String id, String reason, Principal principal) {
        Optional<Section> existing = repository.findById(id);
        if (existing.isPresent()) {
            Section section = existing.get();

            String originalStatus = section.getStatus();
            section.setStatus("PUSHED_BACK");
            section.setReason(reason);
            section.setUpdatedBy(principal.getName());
            section.setUpdatedAt(LocalDate.now());

            repository.save(section);

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("PUSHED_BACK")
                    .description("Section pushed back. Status changed from '" + originalStatus +
                            "' to 'PUSHED_BACK'. Reason: '" + reason + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "section_audit_trail");

            return ResponseEntity.ok("Section pushed back successfully");
        }
        return ResponseEntity.status(404).body("Section not found");
    }

    @Override
    public ResponseEntity getAllPendingSections() {
        List<Section> sections = repository.findByStatus("PENDING");
        return ResponseEntity.ok(sections);
    }

    @Override
    public ResponseEntity getAllApprovedSections() {
        List<Section> sections = repository.findByStatus("APPROVED");
        return ResponseEntity.ok(sections);
    }

    @Override
    public ResponseEntity getAllRejectedSections() {
        List<Section> sections = repository.findByStatus("REJECTED");
        return ResponseEntity.ok(sections);
    }

    @Override
    public ResponseEntity getAllPushedBackSections() {
        List<Section> sections = repository.findByStatus("PUSHED_BACK");
        return ResponseEntity.ok(sections);
    }

    @Override
    public ResponseEntity<String> activateSection(String id, Principal principal) {
        Optional<Section> existing = repository.findById(id);
        if (existing.isPresent()) {
            Section section = existing.get();

            // Check if section is already active
            if (section.isActive()) {
                return ResponseEntity.status(400).body("Section is already active");
            }

            // Check if section is approved before allowing activation
            if (!"APPROVED".equals(section.getStatus())) {
                return ResponseEntity.status(400).body("Section must be approved before it can be activated");
            }

            boolean wasActive = section.isActive();
            section.setActive(true);
            section.setUpdatedBy(principal.getName());
            section.setUpdatedAt(LocalDate.now());

            repository.save(section);

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("ACTIVATED")
                    .description(
                            "Section '" + section.getSectionName() + "' was activated. Active status changed from '"
                                    + wasActive + "' to 'true'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "section_audit_trail");

            return ResponseEntity.ok("Section activated successfully");
        }
        return ResponseEntity.status(404).body("Section not found");
    }

    @Override
    public ResponseEntity<String> deactivateSection(String id, Principal principal) {
        Optional<Section> existing = repository.findById(id);
        if (existing.isPresent()) {
            Section section = existing.get();

            // Check if section is already inactive
            if (!section.isActive()) {
                return ResponseEntity.status(400).body("Section is already inactive");
            }

            boolean wasActive = section.isActive();
            section.setActive(false);
            section.setUpdatedBy(principal.getName());
            section.setUpdatedAt(LocalDate.now());

            repository.save(section);

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("DEACTIVATED")
                    .description(
                            "Section '" + section.getSectionName() + "' was deactivated. Active status changed from '"
                                    + wasActive + "' to 'false'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "section_audit_trail");

            return ResponseEntity.ok("Section deactivated successfully");
        }
        return ResponseEntity.status(404).body("Section not found");
    }

    @Override
    public ResponseEntity<List<Section>> getDeactivatedPendingSections() {
        try {
            List<Section> deactivatedPendingSections = repository.findByActiveFalseAndStatus("PENDING");

            // Create audit trail for this query
            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId("SYSTEM")
                    .action("QUERY")
                    .description("Retrieved " + deactivatedPendingSections.size() +
                            " sections with deactivated status and PENDING approval")
                    .doneBy("SYSTEM")
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "section_audit_trail");

            return ResponseEntity.ok(deactivatedPendingSections);
        } catch (Exception e) {
            // Log error
            UserAuditTrail errorAudit = UserAuditTrail.builder()
                    .userId("SYSTEM")
                    .action("ERROR")
                    .description("Error retrieving deactivated pending sections: " + e.getMessage())
                    .doneBy("SYSTEM")
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(errorAudit, "section_audit_trail");

            return ResponseEntity.status(500).body(null);
        }
    }
}
