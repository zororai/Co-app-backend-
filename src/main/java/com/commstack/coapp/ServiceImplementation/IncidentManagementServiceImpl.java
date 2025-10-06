package com.commstack.coapp.ServiceImplementation;

import com.commstack.coapp.Models.IncidentManagement;
import com.commstack.coapp.Models.UserAuditTrail;
import com.commstack.coapp.Repositories.IncidentManagementRepository;
import com.commstack.coapp.Service.IncidentManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class IncidentManagementServiceImpl implements IncidentManagementService {

    @Autowired
    private IncidentManagementRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public ResponseEntity<IncidentManagement> create(IncidentManagement incident, Principal principal) {

        incident.setStatus("investigating");
        IncidentManagement saved = repository.save(incident);

        UserAuditTrail audit = UserAuditTrail.builder()
                .userId(saved.getId())
                .action("CREATED")
                .description("Incident created: '" + saved.getIncidentTitle() + "' (Severity: '"
                        + saved.getSeverityLevel() + "')")
                .doneBy(principal != null ? principal.getName() : "SYSTEM")
                .dateTime(LocalDateTime.now())
                .build();
        mongoTemplate.save(audit, "incident_audit_trail");

        return ResponseEntity.ok(saved);
    }

    @Override
    public List<IncidentManagement> getAll() {
        return repository.findAll();
    }

    @Override
    public IncidentManagement getById(String id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public ResponseEntity<IncidentManagement> update(String id, IncidentManagement incident, Principal principal) {
        Optional<IncidentManagement> existingOpt = repository.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        IncidentManagement existing = existingOpt.get();
        existing.setIncidentTitle(incident.getIncidentTitle());
        existing.setSeverityLevel(incident.getSeverityLevel());
        existing.setReportedBy(incident.getReportedBy());
        existing.setDescription(incident.getDescription());
        existing.setAttachments(incident.getAttachments());
        existing.setLocation(incident.getLocation());
        existing.setParticipants(incident.getParticipants());

        IncidentManagement updated = repository.save(existing);

        UserAuditTrail audit = UserAuditTrail.builder()
                .userId(id)
                .action("UPDATED")
                .description("Incident updated: '" + updated.getIncidentTitle() + "' (Severity: '"
                        + updated.getSeverityLevel() + "')")
                .doneBy(principal != null ? principal.getName() : "SYSTEM")
                .dateTime(LocalDateTime.now())
                .build();
        mongoTemplate.save(audit, "incident_audit_trail");

        return ResponseEntity.ok(updated);
    }

    @Override
    public ResponseEntity<String> delete(String id, Principal principal) {
        Optional<IncidentManagement> existing = repository.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.status(404).body("Incident not found");
        }

        IncidentManagement incident = existing.get();

        UserAuditTrail audit = UserAuditTrail.builder()
                .userId(id)
                .action("DELETED")
                .description("Incident deleted: '" + incident.getIncidentTitle() + "' (Severity: '"
                        + incident.getSeverityLevel() + "')")
                .doneBy(principal != null ? principal.getName() : "SYSTEM")
                .dateTime(LocalDateTime.now())
                .build();
        mongoTemplate.save(audit, "incident_audit_trail");

        repository.deleteById(id);
        return ResponseEntity.ok("Incident deleted successfully");
    }
}
