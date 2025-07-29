package com.commstack.coapp.ServiceImplementation;

import com.commstack.coapp.Models.BlastingEquipment;
import com.commstack.coapp.Models.UserAuditTrail;
import com.commstack.coapp.Repositories.BlastingEquipmentRepository;
import com.commstack.coapp.Service.BlastingEquipmentService;
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
public class BlastingEquipmentServiceImpl implements BlastingEquipmentService {

    @Autowired
    private BlastingEquipmentRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public ResponseEntity<String> create(BlastingEquipment equipment, Principal principal) {
        equipment.setCreatedBy(principal.getName());
        equipment.setCreatedAt(LocalDate.now());
        equipment.setUpdatedBy(principal.getName());
        equipment.setUpdatedAt(LocalDate.now());
        equipment.setStatus("PENDING");

        BlastingEquipment savedEquipment = repository.save(equipment);

        UserAuditTrail audit = UserAuditTrail.builder()
                .userId(savedEquipment.getId())
                .action("CREATED")
                .description("New blasting equipment created: '" + equipment.getEquipmentName() +
                        "', type: '" + equipment.getEquipmentType() + "'")
                .doneBy(principal.getName())
                .dateTime(LocalDateTime.now())
                .build();
        mongoTemplate.save(audit, "blasting_equipment_audit_trail");

        return ResponseEntity.ok("Blasting equipment created successfully");
    }

    @Override
    public List<BlastingEquipment> getAll() {
        return repository.findAll();
    }

    @Override
    public BlastingEquipment getById(String id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public ResponseEntity<String> update(String id, BlastingEquipment equipment, Principal principal) {
        Optional<BlastingEquipment> existing = repository.findById(id);
        if (existing.isPresent()) {
            BlastingEquipment existingEquipment = existing.get();

            String originalStatus = existingEquipment.getStatus();
            String originalLocation = existingEquipment.getCurrentLocation();
            String originalAssignee = existingEquipment.getAssignedTo();

            // Update fields
            existingEquipment.setEquipmentName(equipment.getEquipmentName());
            existingEquipment.setEquipmentType(equipment.getEquipmentType());
            existingEquipment.setSerialNumber(equipment.getSerialNumber());
            existingEquipment.setManufacturer(equipment.getManufacturer());
            existingEquipment.setModel(equipment.getModel());
            existingEquipment.setPurchaseDate(equipment.getPurchaseDate());
            existingEquipment.setLastMaintenanceDate(equipment.getLastMaintenanceDate());
            existingEquipment.setNextMaintenanceDate(equipment.getNextMaintenanceDate());
            existingEquipment.setMaintenanceStatus(equipment.getMaintenanceStatus());
            existingEquipment.setCurrentLocation(equipment.getCurrentLocation());
            existingEquipment.setAssignedTo(equipment.getAssignedTo());
            existingEquipment.setCondition(equipment.getCondition());
            existingEquipment.setCalibrationStatus(equipment.getCalibrationStatus());
            existingEquipment.setLastCalibrationDate(equipment.getLastCalibrationDate());
            existingEquipment.setNextCalibrationDate(equipment.getNextCalibrationDate());
            existingEquipment.setCertificationNumber(equipment.getCertificationNumber());
            existingEquipment.setCertificationExpiryDate(equipment.getCertificationExpiryDate());
            existingEquipment.setSafetyRating(equipment.getSafetyRating());
            existingEquipment.setOperationalStatus(equipment.getOperationalStatus());
            existingEquipment.setNotes(equipment.getNotes());
            existingEquipment.setStatus(equipment.getStatus());
            existingEquipment.setUpdatedBy(principal.getName());
            existingEquipment.setUpdatedAt(LocalDate.now());

            repository.save(existingEquipment);

            StringBuilder description = new StringBuilder("Equipment updated. Changes: ");
            if (!originalStatus.equals(equipment.getStatus())) {
                description.append("Status changed from '").append(originalStatus)
                        .append("' to '").append(equipment.getStatus()).append("'. ");
            }
            if (!originalLocation.equals(equipment.getCurrentLocation())) {
                description.append("Location changed from '").append(originalLocation)
                        .append("' to '").append(equipment.getCurrentLocation()).append("'. ");
            }
            if (!originalAssignee.equals(equipment.getAssignedTo())) {
                description.append("Assignee changed from '").append(originalAssignee)
                        .append("' to '").append(equipment.getAssignedTo()).append("'.");
            }

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("UPDATED")
                    .description(description.toString())
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "blasting_equipment_audit_trail");

            return ResponseEntity.ok("Blasting equipment updated successfully");
        }
        return ResponseEntity.status(404).body("Equipment not found");
    }

    @Override
    public ResponseEntity<String> delete(String id, Principal principal) {
        Optional<BlastingEquipment> equipmentToDelete = repository.findById(id);
        if (equipmentToDelete.isPresent()) {
            BlastingEquipment equipment = equipmentToDelete.get();

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("DELETED")
                    .description("Equipment deleted: '" + equipment.getEquipmentName() +
                            "', type: '" + equipment.getEquipmentType() + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "blasting_equipment_audit_trail");

            repository.deleteById(id);
            return ResponseEntity.ok("Equipment deleted successfully");
        }
        return ResponseEntity.status(404).body("Equipment not found");
    }

    @Override
    public ResponseEntity<String> approval(String id, Principal principal) {
        Optional<BlastingEquipment> existing = repository.findById(id);
        if (existing.isPresent()) {
            BlastingEquipment equipment = existing.get();

            if (!"PENDING".equals(equipment.getStatus())) {
                return ResponseEntity.status(400).body("Equipment is not in PENDING status");
            }

            String originalStatus = equipment.getStatus();
            equipment.setStatus("APPROVED");
            equipment.setUpdatedBy(principal.getName());
            equipment.setUpdatedAt(LocalDate.now());

            repository.save(equipment);

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("APPROVED")
                    .description("Equipment status changed from '" + originalStatus +
                            "' to 'APPROVED' for: '" + equipment.getEquipmentName() + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "blasting_equipment_audit_trail");

            return ResponseEntity.ok("Equipment approved successfully");
        }
        return ResponseEntity.status(404).body("Equipment not found");
    }

    @Override
    public ResponseEntity<String> reject(String id, String reason, Principal principal) {
        Optional<BlastingEquipment> existing = repository.findById(id);
        if (existing.isPresent()) {
            BlastingEquipment equipment = existing.get();

            String originalStatus = equipment.getStatus();
            equipment.setStatus("REJECTED");
            equipment.setReason(reason);
            equipment.setUpdatedBy(principal.getName());
            equipment.setUpdatedAt(LocalDate.now());

            repository.save(equipment);

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("REJECTED")
                    .description("Equipment rejected. Status changed from '" + originalStatus +
                            "' to 'REJECTED'. Reason: '" + reason + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "blasting_equipment_audit_trail");

            return ResponseEntity.ok("Equipment rejected successfully");
        }
        return ResponseEntity.status(404).body("Equipment not found");
    }

    @Override
    public ResponseEntity<String> pushBack(String id, String reason, Principal principal) {
        Optional<BlastingEquipment> existing = repository.findById(id);
        if (existing.isPresent()) {
            BlastingEquipment equipment = existing.get();

            String originalStatus = equipment.getStatus();
            equipment.setStatus("PUSHED_BACK");
            equipment.setReason(reason);
            equipment.setUpdatedBy(principal.getName());
            equipment.setUpdatedAt(LocalDate.now());

            repository.save(equipment);

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("PUSHED_BACK")
                    .description("Equipment pushed back. Status changed from '" + originalStatus +
                            "' to 'PUSHED_BACK'. Reason: '" + reason + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "blasting_equipment_audit_trail");

            return ResponseEntity.ok("Equipment pushed back successfully");
        }
        return ResponseEntity.status(404).body("Equipment not found");
    }

    @Override
    public List<BlastingEquipment> findByEquipmentType(String equipmentType) {
        return repository.findByEquipmentType(equipmentType);
    }

    @Override
    public List<BlastingEquipment> findByManufacturer(String manufacturer) {
        return repository.findByManufacturer(manufacturer);
    }

    @Override
    public List<BlastingEquipment> findByAssignedTo(String assignedTo) {
        return repository.findByAssignedTo(assignedTo);
    }

    @Override
    public List<BlastingEquipment> findByLocation(String location) {
        return repository.findByCurrentLocation(location);
    }

    @Override
    public List<BlastingEquipment> findByMaintenanceStatus(String maintenanceStatus) {
        return repository.findByMaintenanceStatus(maintenanceStatus);
    }

    @Override
    public List<BlastingEquipment> findByOperationalStatus(String operationalStatus) {
        return repository.findByOperationalStatus(operationalStatus);
    }

    @Override
    public List<BlastingEquipment> findMaintenanceDue(LocalDate date) {
        return repository.findByNextMaintenanceDateBefore(date);
    }

    @Override
    public List<BlastingEquipment> findCalibrationDue(LocalDate date) {
        return repository.findByNextCalibrationDateBefore(date);
    }

    @Override
    public List<BlastingEquipment> findCertificationExpiring(LocalDate date) {
        return repository.findByCertificationExpiryDateBefore(date);
    }

    @Override
    public ResponseEntity<String> getAllPendingEquipment() {
        List<BlastingEquipment> equipment = repository.findByStatus("PENDING");
        return ResponseEntity.ok(equipment.toString());
    }

    @Override
    public ResponseEntity<String> getAllApprovedEquipment() {
        List<BlastingEquipment> equipment = repository.findByStatus("APPROVED");
        return ResponseEntity.ok(equipment.toString());
    }

    @Override
    public ResponseEntity<String> getAllRejectedEquipment() {
        List<BlastingEquipment> equipment = repository.findByStatus("REJECTED");
        return ResponseEntity.ok(equipment.toString());
    }

    @Override
    public ResponseEntity<String> getAllPushedBackEquipment() {
        List<BlastingEquipment> equipment = repository.findByStatus("PUSHED_BACK");
        return ResponseEntity.ok(equipment.toString());
    }
}
