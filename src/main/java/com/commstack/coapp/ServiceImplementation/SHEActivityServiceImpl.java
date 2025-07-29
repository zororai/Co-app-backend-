package com.commstack.coapp.ServiceImplementation;

import com.commstack.coapp.Models.SHEActivity;
import com.commstack.coapp.Models.UserAuditTrail;
import com.commstack.coapp.Repositories.SHEActivityRepository;
import com.commstack.coapp.Service.SHEActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SHEActivityServiceImpl implements SHEActivityService {

    @Autowired
    private SHEActivityRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public ResponseEntity<String> create(SHEActivity activity, Principal principal) {
        activity.setCreatedBy(principal.getName());
        activity.setCreatedAt(LocalDate.now());
        activity.setUpdatedBy(principal.getName());
        activity.setUpdatedAt(LocalDate.now());
        activity.setStatus("PENDING");

        SHEActivity savedActivity = repository.save(activity);

        UserAuditTrail audit = UserAuditTrail.builder()
                .userId(savedActivity.getId())
                .action("CREATED")
                .description("New SHE activity created: '" + activity.getActivityTitle() +
                        "', type: '" + activity.getActivityType() + "'")
                .doneBy(principal.getName())
                .dateTime(LocalDateTime.now())
                .build();
        mongoTemplate.save(audit, "she_activity_audit_trail");

        return ResponseEntity.ok("SHE activity created successfully");
    }

    @Override
    public List<SHEActivity> getAll() {
        return repository.findAll();
    }

    @Override
    public SHEActivity getById(String id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public ResponseEntity<String> update(String id, SHEActivity activity, Principal principal) {
        Optional<SHEActivity> existing = repository.findById(id);
        if (existing.isPresent()) {
            SHEActivity existingActivity = existing.get();

            String originalStatus = existingActivity.getStatus();
            String originalRiskLevel = existingActivity.getRiskLevel();

            // Update fields
            existingActivity.setActivityTitle(activity.getActivityTitle());
            existingActivity.setActivityType(activity.getActivityType());
            existingActivity.setDescription(activity.getDescription());
            existingActivity.setDate(activity.getDate());
            existingActivity.setStartTime(activity.getStartTime());
            existingActivity.setEndTime(activity.getEndTime());
            existingActivity.setLocation(activity.getLocation());
            existingActivity.setConductedBy(activity.getConductedBy());
            existingActivity.setParticipants(activity.getParticipants());
            existingActivity.setDepartment(activity.getDepartment());
            existingActivity.setSupervisor(activity.getSupervisor());
            existingActivity.setRiskLevel(activity.getRiskLevel());
            existingActivity.setHazardsIdentified(activity.getHazardsIdentified());
            existingActivity.setControlMeasures(activity.getControlMeasures());
            existingActivity.setPpeRequired(activity.isPpeRequired());
            existingActivity.setRequiredPPE(activity.getRequiredPPE());
            existingActivity.setRegulatoryReference(activity.getRegulatoryReference());
            existingActivity.setObservations(activity.getObservations());
            existingActivity.setNonConformities(activity.getNonConformities());
            existingActivity.setCorrectiveActions(activity.getCorrectiveActions());
            existingActivity.setPreventiveActions(activity.getPreventiveActions());
            existingActivity.setDeadlineForActions(activity.getDeadlineForActions());
            existingActivity.setResponsiblePerson(activity.getResponsiblePerson());
            existingActivity.setDocumentReference(activity.getDocumentReference());
            existingActivity.setAttachments(activity.getAttachments());
            existingActivity.setPhotos(activity.getPhotos());
            existingActivity.setFollowUpRequired(activity.isFollowUpRequired());
            existingActivity.setFollowUpDate(activity.getFollowUpDate());
            existingActivity.setFollowUpAssignedTo(activity.getFollowUpAssignedTo());
            existingActivity.setFollowUpStatus(activity.getFollowUpStatus());
            existingActivity.setStatus(activity.getStatus());
            existingActivity.setApprovalComments(activity.getApprovalComments());
            existingActivity.setSafetyScore(activity.getSafetyScore());
            existingActivity.setComplianceScore(activity.getComplianceScore());
            existingActivity.setParticipationRate(activity.getParticipationRate());
            existingActivity.setUpdatedBy(principal.getName());
            existingActivity.setUpdatedAt(LocalDate.now());

            repository.save(existingActivity);

            StringBuilder description = new StringBuilder("SHE activity updated. Changes: ");
            if (!originalStatus.equals(activity.getStatus())) {
                description.append("Status changed from '").append(originalStatus)
                        .append("' to '").append(activity.getStatus()).append("'. ");
            }
            if (!originalRiskLevel.equals(activity.getRiskLevel())) {
                description.append("Risk level changed from '").append(originalRiskLevel)
                        .append("' to '").append(activity.getRiskLevel()).append("'.");
            }

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("UPDATED")
                    .description(description.toString())
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "she_activity_audit_trail");

            return ResponseEntity.ok("SHE activity updated successfully");
        }
        return ResponseEntity.status(404).body("SHE activity not found");
    }

    @Override
    public ResponseEntity<String> delete(String id, Principal principal) {
        Optional<SHEActivity> activityToDelete = repository.findById(id);
        if (activityToDelete.isPresent()) {
            SHEActivity activity = activityToDelete.get();

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("DELETED")
                    .description("SHE activity deleted: '" + activity.getActivityTitle() +
                            "', type: '" + activity.getActivityType() + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "she_activity_audit_trail");

            repository.deleteById(id);
            return ResponseEntity.ok("SHE activity deleted successfully");
        }
        return ResponseEntity.status(404).body("SHE activity not found");
    }

    @Override
    public ResponseEntity<String> approval(String id, Principal principal) {
        Optional<SHEActivity> existing = repository.findById(id);
        if (existing.isPresent()) {
            SHEActivity activity = existing.get();

            if (!"PENDING".equals(activity.getStatus())) {
                return ResponseEntity.status(400).body("SHE activity is not in PENDING status");
            }

            String originalStatus = activity.getStatus();
            activity.setStatus("APPROVED");
            activity.setUpdatedBy(principal.getName());
            activity.setUpdatedAt(LocalDate.now());

            repository.save(activity);

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("APPROVED")
                    .description("SHE activity status changed from '" + originalStatus +
                            "' to 'APPROVED' for: '" + activity.getActivityTitle() + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "she_activity_audit_trail");

            return ResponseEntity.ok("SHE activity approved successfully");
        }
        return ResponseEntity.status(404).body("SHE activity not found");
    }

    @Override
    public ResponseEntity<String> reject(String id, String reason, Principal principal) {
        Optional<SHEActivity> existing = repository.findById(id);
        if (existing.isPresent()) {
            SHEActivity activity = existing.get();

            String originalStatus = activity.getStatus();
            activity.setStatus("REJECTED");
            activity.setReason(reason);
            activity.setUpdatedBy(principal.getName());
            activity.setUpdatedAt(LocalDate.now());

            repository.save(activity);

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("REJECTED")
                    .description("SHE activity rejected. Status changed from '" + originalStatus +
                            "' to 'REJECTED'. Reason: '" + reason + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "she_activity_audit_trail");

            return ResponseEntity.ok("SHE activity rejected successfully");
        }
        return ResponseEntity.status(404).body("SHE activity not found");
    }

    @Override
    public ResponseEntity<String> pushBack(String id, String reason, Principal principal) {
        Optional<SHEActivity> existing = repository.findById(id);
        if (existing.isPresent()) {
            SHEActivity activity = existing.get();

            String originalStatus = activity.getStatus();
            activity.setStatus("PUSHED_BACK");
            activity.setReason(reason);
            activity.setUpdatedBy(principal.getName());
            activity.setUpdatedAt(LocalDate.now());

            repository.save(activity);

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("PUSHED_BACK")
                    .description("SHE activity pushed back. Status changed from '" + originalStatus +
                            "' to 'PUSHED_BACK'. Reason: '" + reason + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "she_activity_audit_trail");

            return ResponseEntity.ok("SHE activity pushed back successfully");
        }
        return ResponseEntity.status(404).body("SHE activity not found");
    }

    @Override
    public ResponseEntity<String> complete(String id, Principal principal) {
        Optional<SHEActivity> existing = repository.findById(id);
        if (existing.isPresent()) {
            SHEActivity activity = existing.get();

            String originalStatus = activity.getStatus();
            activity.setStatus("COMPLETED");
            activity.setUpdatedBy(principal.getName());
            activity.setUpdatedAt(LocalDate.now());

            repository.save(activity);

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("COMPLETED")
                    .description("SHE activity completed. Status changed from '" + originalStatus +
                            "' to 'COMPLETED'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "she_activity_audit_trail");

            return ResponseEntity.ok("SHE activity marked as completed successfully");
        }
        return ResponseEntity.status(404).body("SHE activity not found");
    }

    @Override
    public List<SHEActivity> findByActivityType(String activityType) {
        return repository.findByActivityType(activityType);
    }

    @Override
    public List<SHEActivity> findByDate(LocalDate date) {
        return repository.findByDate(date);
    }

    @Override
    public List<SHEActivity> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return repository.findByDateBetween(startDate, endDate);
    }

    @Override
    public List<SHEActivity> findByLocation(String location) {
        return repository.findByLocation(location);
    }

    @Override
    public List<SHEActivity> findByConductedBy(String conductedBy) {
        return repository.findByConductedBy(conductedBy);
    }

    @Override
    public List<SHEActivity> findByDepartment(String department) {
        return repository.findByDepartment(department);
    }

    @Override
    public List<SHEActivity> findBySupervisor(String supervisor) {
        return repository.findBySupervisor(supervisor);
    }

    @Override
    public List<SHEActivity> findByRiskLevel(String riskLevel) {
        return repository.findByRiskLevel(riskLevel);
    }

    @Override
    public List<SHEActivity> findActivitiesRequiringFollowUp() {
        return repository.findByFollowUpRequired(true);
    }

    @Override
    public List<SHEActivity> findOverdueFollowUps(LocalDate date) {
        return repository.findByFollowUpDateBeforeAndFollowUpStatus(date, "PENDING");
    }

    @Override
    public List<SHEActivity> findOverdueActions(LocalDate date) {
        return repository.findByDeadlineForActionsBeforeAndStatus(date, "APPROVED");
    }

    @Override
    public ResponseEntity<String> getComplianceMetrics(LocalDate startDate, LocalDate endDate) {
        List<SHEActivity> activities = repository.findByDateBetween(startDate, endDate);
        double avgComplianceScore = activities.stream()
                .mapToInt(SHEActivity::getComplianceScore)
                .average()
                .orElse(0.0);
        return ResponseEntity.ok("Average compliance score: " + avgComplianceScore);
    }

    @Override
    public ResponseEntity<String> getSafetyMetrics(LocalDate startDate, LocalDate endDate) {
        List<SHEActivity> activities = repository.findByDateBetween(startDate, endDate);
        double avgSafetyScore = activities.stream()
                .mapToInt(SHEActivity::getSafetyScore)
                .average()
                .orElse(0.0);
        return ResponseEntity.ok("Average safety score: " + avgSafetyScore);
    }

    @Override
    public ResponseEntity<String> getParticipationMetrics(LocalDate startDate, LocalDate endDate) {
        List<SHEActivity> activities = repository.findByDateBetween(startDate, endDate);
        double avgParticipationRate = activities.stream()
                .mapToInt(SHEActivity::getParticipationRate)
                .average()
                .orElse(0.0);
        return ResponseEntity.ok("Average participation rate: " + avgParticipationRate);
    }

    @Override
    public ResponseEntity<String> getAllPendingActivities() {
        List<SHEActivity> activities = repository.findByStatus("PENDING");
        return ResponseEntity.ok(activities.toString());
    }

    @Override
    public ResponseEntity<String> getAllApprovedActivities() {
        List<SHEActivity> activities = repository.findByStatus("APPROVED");
        return ResponseEntity.ok(activities.toString());
    }

    @Override
    public ResponseEntity<String> getAllRejectedActivities() {
        List<SHEActivity> activities = repository.findByStatus("REJECTED");
        return ResponseEntity.ok(activities.toString());
    }

    @Override
    public ResponseEntity<String> getAllPushedBackActivities() {
        List<SHEActivity> activities = repository.findByStatus("PUSHED_BACK");
        return ResponseEntity.ok(activities.toString());
    }

    @Override
    public ResponseEntity<String> getAllCompletedActivities() {
        List<SHEActivity> activities = repository.findByStatus("COMPLETED");
        return ResponseEntity.ok(activities.toString());
    }
}
