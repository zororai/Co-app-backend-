package com.commstack.coapp.Service;

import com.commstack.coapp.Models.BlastingEquipment;
import org.springframework.http.ResponseEntity;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

public interface BlastingEquipmentService {
    ResponseEntity<String> create(BlastingEquipment equipment, Principal principal);

    List<BlastingEquipment> getAll();

    BlastingEquipment getById(String id);

    ResponseEntity<String> update(String id, BlastingEquipment equipment, Principal principal);

    ResponseEntity<String> delete(String id, Principal principal);

    // Status management
    ResponseEntity<String> approval(String id, Principal principal);

    ResponseEntity<String> reject(String id, String reason, Principal principal);

    ResponseEntity<String> pushBack(String id, String reason, Principal principal);

    // Status queries
    ResponseEntity<String> getAllPendingEquipment();

    ResponseEntity<String> getAllApprovedEquipment();

    ResponseEntity<String> getAllRejectedEquipment();

    ResponseEntity<String> getAllPushedBackEquipment();

    // Specialized queries
    List<BlastingEquipment> findByEquipmentType(String equipmentType);

    List<BlastingEquipment> findByManufacturer(String manufacturer);

    List<BlastingEquipment> findByAssignedTo(String assignedTo);

    List<BlastingEquipment> findByLocation(String location);

    List<BlastingEquipment> findByMaintenanceStatus(String maintenanceStatus);

    List<BlastingEquipment> findByOperationalStatus(String operationalStatus);

    // Maintenance and certification queries
    List<BlastingEquipment> findMaintenanceDue(LocalDate date);

    List<BlastingEquipment> findCalibrationDue(LocalDate date);

    List<BlastingEquipment> findCertificationExpiring(LocalDate date);
}
