package com.commstack.coapp.ServiceImplementation;

import com.commstack.coapp.Models.BlastSchedule;
import com.commstack.coapp.Models.UserAuditTrail;
import com.commstack.coapp.Repositories.BlastScheduleRepository;
import com.commstack.coapp.Service.BlastScheduleService;
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
public class BlastScheduleServiceImpl implements BlastScheduleService {

    @Autowired
    private BlastScheduleRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public ResponseEntity<String> create(BlastSchedule schedule, Principal principal) {
        schedule.setCreatedBy(principal.getName());
        schedule.setCreatedAt(LocalDate.now());
        schedule.setUpdatedBy(principal.getName());
        schedule.setUpdatedAt(LocalDate.now());
        schedule.setStatus("PENDING");

        BlastSchedule savedSchedule = repository.save(schedule);

        UserAuditTrail audit = UserAuditTrail.builder()
                .userId(savedSchedule.getId())
                .action("CREATED")
                .description("New blast event scheduled: '" + schedule.getEventTitle() +
                        "' at location: '" + schedule.getSpecificLocation() + "'")
                .doneBy(principal.getName())
                .dateTime(LocalDateTime.now())
                .build();
        mongoTemplate.save(audit, "blast_schedule_audit_trail");

        return ResponseEntity.ok("Blast schedule created successfully");
    }

    @Override
    public List<BlastSchedule> getAll() {
        return repository.findAll();
    }

    @Override
    public BlastSchedule getById(String id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<BlastSchedule> findByBlastZone(String blastZone) {
        return repository.findByBlastZone(blastZone);
    }

    @Override
    public List<BlastSchedule> findByDate(LocalDate date) {
        return repository.findByDate(date);
    }

    @Override
    public List<BlastSchedule> findByAssignedOperator(String assignedOperator) {
        return repository.findByAssignedOperator(assignedOperator);
    }

    @Override
    public List<BlastSchedule> findBySafetyOfficer(String safetyOfficer) {
        return repository.findBySafetyOfficer(safetyOfficer);
    }

    @Override
    public List<BlastSchedule> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return repository.findByDateBetween(startDate, endDate);
    }

    @Override
    public List<BlastSchedule> findByBlastType(String blastType) {
        return repository.findByBlastType(blastType);
    }

    @Override
    public ResponseEntity<String> update(String id, BlastSchedule schedule, Principal principal) {
        Optional<BlastSchedule> existing = repository.findById(id);
        if (existing.isPresent()) {
            BlastSchedule existingSchedule = existing.get();

            String originalStatus = existingSchedule.getStatus();

            // Update fields
            existingSchedule.setEventTitle(schedule.getEventTitle());
            existingSchedule.setBlastType(schedule.getBlastType());
            existingSchedule.setDate(schedule.getDate());
            existingSchedule.setTime(schedule.getTime());
            existingSchedule.setDuration(schedule.getDuration());
            existingSchedule.setBlastZone(schedule.getBlastZone());
            existingSchedule.setSpecificLocation(schedule.getSpecificLocation());
            existingSchedule.setAssignedOperator(schedule.getAssignedOperator());
            existingSchedule.setSafetyOfficer(schedule.getSafetyOfficer());
            existingSchedule.setExplosiveType(schedule.getExplosiveType());
            existingSchedule.setQuantity(schedule.getQuantity());
            existingSchedule.setEvacuationRadius(schedule.getEvacuationRadius());
            existingSchedule.setNotifySHE(schedule.isNotifySHE());
            existingSchedule.setNotifyTransport(schedule.isNotifyTransport());
            existingSchedule.setNotifySecurity(schedule.isNotifySecurity());
            existingSchedule.setNotifyMaintenance(schedule.isNotifyMaintenance());
            existingSchedule.setNotifyRecoveryTeam(schedule.isNotifyRecoveryTeam());
            existingSchedule.setNotifyEnvironmental(schedule.isNotifyEnvironmental());
            existingSchedule.setWeatherRequirements(schedule.getWeatherRequirements());
            existingSchedule.setStatus(schedule.getStatus());
            existingSchedule.setUpdatedBy(principal.getName());
            existingSchedule.setUpdatedAt(LocalDate.now());

            repository.save(existingSchedule);

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("UPDATED")
                    .description("Blast schedule updated. Status changed from '" + originalStatus +
                            "' to '" + schedule.getStatus() + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "blast_schedule_audit_trail");

            return ResponseEntity.ok("Blast schedule updated successfully");
        }
        return ResponseEntity.status(404).body("Blast schedule not found");
    }

    @Override
    public ResponseEntity<String> delete(String id, Principal principal) {
        Optional<BlastSchedule> scheduleToDelete = repository.findById(id);
        if (scheduleToDelete.isPresent()) {
            BlastSchedule schedule = scheduleToDelete.get();

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("DELETED")
                    .description("Blast schedule deleted: '" + schedule.getEventTitle() +
                            "' at location: '" + schedule.getSpecificLocation() + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "blast_schedule_audit_trail");

            repository.deleteById(id);
            return ResponseEntity.ok("Blast schedule deleted successfully");
        }
        return ResponseEntity.status(404).body("Blast schedule not found");
    }

    @Override
    public ResponseEntity<String> approval(String id, Principal principal) {
        Optional<BlastSchedule> existing = repository.findById(id);
        if (existing.isPresent()) {
            BlastSchedule schedule = existing.get();

            if (!"PENDING".equals(schedule.getStatus())) {
                return ResponseEntity.status(400).body("Blast schedule is not in PENDING status");
            }

            String originalStatus = schedule.getStatus();
            schedule.setStatus("APPROVED");
            schedule.setUpdatedBy(principal.getName());
            schedule.setUpdatedAt(LocalDate.now());

            repository.save(schedule);

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("APPROVED")
                    .description("Blast schedule status changed from '" + originalStatus +
                            "' to 'APPROVED' for event: '" + schedule.getEventTitle() + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "blast_schedule_audit_trail");

            return ResponseEntity.ok("Blast schedule approved successfully");
        }
        return ResponseEntity.status(404).body("Blast schedule not found");
    }

    @Override
    public ResponseEntity<String> reject(String id, String reason, Principal principal) {
        Optional<BlastSchedule> existing = repository.findById(id);
        if (existing.isPresent()) {
            BlastSchedule schedule = existing.get();

            String originalStatus = schedule.getStatus();
            schedule.setStatus("REJECTED");
            schedule.setReason(reason);
            schedule.setUpdatedBy(principal.getName());
            schedule.setUpdatedAt(LocalDate.now());

            repository.save(schedule);

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("REJECTED")
                    .description("Blast schedule rejected. Status changed from '" + originalStatus +
                            "' to 'REJECTED'. Reason: '" + reason + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "blast_schedule_audit_trail");

            return ResponseEntity.ok("Blast schedule rejected successfully");
        }
        return ResponseEntity.status(404).body("Blast schedule not found");
    }

    @Override
    public ResponseEntity<String> pushBack(String id, String reason, Principal principal) {
        Optional<BlastSchedule> existing = repository.findById(id);
        if (existing.isPresent()) {
            BlastSchedule schedule = existing.get();

            String originalStatus = schedule.getStatus();
            schedule.setStatus("PUSHED_BACK");
            schedule.setReason(reason);
            schedule.setUpdatedBy(principal.getName());
            schedule.setUpdatedAt(LocalDate.now());

            repository.save(schedule);

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("PUSHED_BACK")
                    .description("Blast schedule pushed back. Status changed from '" + originalStatus +
                            "' to 'PUSHED_BACK'. Reason: '" + reason + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "blast_schedule_audit_trail");

            return ResponseEntity.ok("Blast schedule pushed back successfully");
        }
        return ResponseEntity.status(404).body("Blast schedule not found");
    }

    @Override
    public ResponseEntity<String> getAllPendingSchedules() {
        List<BlastSchedule> schedules = repository.findByStatus("PENDING");
        return ResponseEntity.ok(schedules.toString());
    }

    @Override
    public ResponseEntity<String> getAllApprovedSchedules() {
        List<BlastSchedule> schedules = repository.findByStatus("APPROVED");
        return ResponseEntity.ok(schedules.toString());
    }

    @Override
    public ResponseEntity<String> getAllRejectedSchedules() {
        List<BlastSchedule> schedules = repository.findByStatus("REJECTED");
        return ResponseEntity.ok(schedules.toString());
    }

    @Override
    public ResponseEntity<String> getAllPushedBackSchedules() {
        List<BlastSchedule> schedules = repository.findByStatus("PUSHED_BACK");
        return ResponseEntity.ok(schedules.toString());
    }
}
