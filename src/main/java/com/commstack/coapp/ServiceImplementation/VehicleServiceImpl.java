
package com.commstack.coapp.ServiceImplementation;

import com.commstack.coapp.Models.Vehicle;
import com.commstack.coapp.Models.UserAuditTrail;
import com.commstack.coapp.Repositories.VehicleRepository;
import com.commstack.coapp.Service.VehicleService;
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
public class VehicleServiceImpl implements VehicleService {

    public ResponseEntity<String> setToInTransit(String id, Principal principal) {
        Optional<Vehicle> existing = repository.findById(id);
        if (existing.isPresent()) {
            Vehicle vehicle = existing.get();
            vehicle.setOperationalStatus("in transit");
            vehicle.setUpdatedBy(principal.getName());
            vehicle.setUpdatedAt(LocalDate.now());
            repository.save(vehicle);
            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("IN_TRANSIT")
                    .description("Vehicle operational status set to 'in transit' for Registration Number: '"
                            + vehicle.getRegNumber() + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "vehicle_audit_trail");
            return ResponseEntity.ok("Vehicle operational status updated to in transit");
        }
        return ResponseEntity.status(404).body("Vehicle not found");
    }

    public ResponseEntity<String> setToLoading(String id, Principal principal) {
        Optional<Vehicle> existing = repository.findById(id);
        if (existing.isPresent()) {
            Vehicle vehicle = existing.get();
            vehicle.setOperationalStatus("Loading");
            vehicle.setUpdatedBy(principal.getName());
            vehicle.setUpdatedAt(LocalDate.now());
            repository.save(vehicle);
            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("LOADING")
                    .description("Vehicle operational status set to 'Loading' for Registration Number: '"
                            + vehicle.getRegNumber() + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "vehicle_audit_trail");
            return ResponseEntity.ok("Vehicle operational status updated to Loading");
        }
        return ResponseEntity.status(404).body("Vehicle not found");
    }

    public ResponseEntity<String> setToIdle(String id, Principal principal) {
        Optional<Vehicle> existing = repository.findById(id);
        if (existing.isPresent()) {
            Vehicle vehicle = existing.get();
            vehicle.setOperationalStatus("idle");
            vehicle.setUpdatedBy(principal.getName());
            vehicle.setUpdatedAt(LocalDate.now());
            repository.save(vehicle);
            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("IDLE")
                    .description("Vehicle operational status set to 'idle' for Registration Number: '"
                            + vehicle.getRegNumber() + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "vehicle_audit_trail");
            return ResponseEntity.ok("Vehicle operational status updated to idle");
        }
        return ResponseEntity.status(404).body("Vehicle not found");
    }

    public ResponseEntity<String> setToMaintainance(String id, Principal principal) {
        Optional<Vehicle> existing = repository.findById(id);
        if (existing.isPresent()) {
            Vehicle vehicle = existing.get();
            vehicle.setOperationalStatus("Maintainance");
            vehicle.setUpdatedBy(principal.getName());
            vehicle.setUpdatedAt(LocalDate.now());
            repository.save(vehicle);
            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("MAINTAINANCE")
                    .description("Vehicle operational status set to 'Maintainance' for Registration Number: '"
                            + vehicle.getRegNumber() + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "vehicle_audit_trail");
            return ResponseEntity.ok("Vehicle operational status updated to Maintainance");
        }
        return ResponseEntity.status(404).body("Vehicle not found");
    }

    @Autowired
    private VehicleRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public ResponseEntity<String> create(Vehicle vehicle, Principal principal) {
        // Check if vehicle with same registration number already exists
        if (repository.findByRegNumber(vehicle.getRegNumber()) != null) {
            return ResponseEntity.badRequest().body("Vehicle with this registration number already exists");
        }
        vehicle.setOperationalStatus("idle");
        vehicle.setCreatedBy(principal.getName());
        vehicle.setCreatedAt(LocalDate.now());
        vehicle.setUpdatedBy(principal.getName());
        vehicle.setUpdatedAt(LocalDate.now());
        vehicle.setStatus("PENDING");
        // 'Idle' 'Loading' 'Loaded' 'Maintainance
        Vehicle savedVehicle = repository.save(vehicle);

        UserAuditTrail audit = UserAuditTrail.builder()
                .userId(savedVehicle.getId())
                .action("CREATED")
                .description("New vehicle registered: Registration Number '" + vehicle.getRegNumber() +
                        "', Owner: '" + vehicle.getOwnerName() + "'")
                .doneBy(principal.getName())
                .dateTime(LocalDateTime.now())
                .build();
        mongoTemplate.save(audit, "vehicle_audit_trail");

        return ResponseEntity.ok("Vehicle registered successfully");
    }

    @Override
    public List<Vehicle> getAll() {
        return repository.findAll();
    }

    public List<Vehicle> getAllIdleVehicles() {
        return repository.findAll().stream()
                .filter(v -> "idle".equalsIgnoreCase(v.getOperationalStatus()))
                .toList();
    }

    @Override
    public Vehicle getById(String id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Vehicle findByRegNumber(String regNumber) {
        return repository.findByRegNumber(regNumber);
    }

    @Override
    public List<Vehicle> findByOwnerName(String ownerName) {
        return repository.findByOwnerName(ownerName);
    }

    @Override
    public Vehicle findByOwnerIdNumber(String ownerIdNumber) {
        return repository.findByOwnerIdNumber(ownerIdNumber);
    }

    @Override
    public ResponseEntity<String> update(String id, Vehicle vehicle, Principal principal) {
        Optional<Vehicle> existing = repository.findById(id);
        if (existing.isPresent()) {
            Vehicle existingVehicle = existing.get();

            // Check if registration number is being changed and new reg number already
            // exists
            if (!existingVehicle.getRegNumber().equals(vehicle.getRegNumber()) &&
                    repository.findByRegNumber(vehicle.getRegNumber()) != null) {
                return ResponseEntity.badRequest().body("Vehicle with this registration number already exists");
            }

            String originalStatus = existingVehicle.getStatus();

            // Update fields
            existingVehicle.setRegNumber(vehicle.getRegNumber());
            existingVehicle.setOwnerName(vehicle.getOwnerName());
            existingVehicle.setOwnerAddress(vehicle.getOwnerAddress());
            existingVehicle.setOwnerCellNumber(vehicle.getOwnerCellNumber());
            existingVehicle.setOwnerIdNumber(vehicle.getOwnerIdNumber());
            existingVehicle.setIdPicture(vehicle.getIdPicture());
            existingVehicle.setTruckPicture(vehicle.getTruckPicture());
            existingVehicle.setRegistrationBook(vehicle.getRegistrationBook());
            existingVehicle.setStatus(vehicle.getStatus());
            existingVehicle.setUpdatedBy(principal.getName());
            existingVehicle.setUpdatedAt(LocalDate.now());

            repository.save(existingVehicle);

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("UPDATED")
                    .description("Vehicle updated. Status changed from '" + originalStatus +
                            "' to '" + vehicle.getStatus() + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "vehicle_audit_trail");

            return ResponseEntity.ok("Vehicle updated successfully");
        }
        return ResponseEntity.status(404).body("Vehicle not found");
    }

    @Override
    public ResponseEntity<String> delete(String id, Principal principal) {
        Optional<Vehicle> vehicleToDelete = repository.findById(id);
        if (vehicleToDelete.isPresent()) {
            Vehicle vehicle = vehicleToDelete.get();

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("DELETED")
                    .description("Vehicle deleted: Registration Number '" + vehicle.getRegNumber() +
                            "', Owner: '" + vehicle.getOwnerName() + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "vehicle_audit_trail");

            repository.deleteById(id);
            return ResponseEntity.ok("Vehicle deleted successfully");
        }
        return ResponseEntity.status(404).body("Vehicle not found");
    }

    @Override
    public ResponseEntity<String> approval(String id, Principal principal) {
        Optional<Vehicle> existing = repository.findById(id);
        if (existing.isPresent()) {
            Vehicle vehicle = existing.get();

            if (!"PENDING".equals(vehicle.getStatus())) {
                return ResponseEntity.status(400).body("Vehicle is not in PENDING status");
            }

            String originalStatus = vehicle.getStatus();
            vehicle.setStatus("APPROVED");
            vehicle.setUpdatedBy(principal.getName());
            vehicle.setUpdatedAt(LocalDate.now());

            repository.save(vehicle);

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("APPROVED")
                    .description("Vehicle status changed from '" + originalStatus +
                            "' to 'APPROVED' for Registration Number: '" + vehicle.getRegNumber() + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "vehicle_audit_trail");

            return ResponseEntity.ok("Vehicle approved successfully");
        }
        return ResponseEntity.status(404).body("Vehicle not found");
    }

    @Override
    public ResponseEntity<String> reject(String id, String reason, Principal principal) {
        Optional<Vehicle> existing = repository.findById(id);
        if (existing.isPresent()) {
            Vehicle vehicle = existing.get();

            String originalStatus = vehicle.getStatus();
            vehicle.setStatus("REJECTED");
            vehicle.setReason(reason);
            vehicle.setUpdatedBy(principal.getName());
            vehicle.setUpdatedAt(LocalDate.now());

            repository.save(vehicle);

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("REJECTED")
                    .description("Vehicle rejected. Status changed from '" + originalStatus +
                            "' to 'REJECTED'. Reason: '" + reason + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "vehicle_audit_trail");

            return ResponseEntity.ok("Vehicle rejected successfully");
        }
        return ResponseEntity.status(404).body("Vehicle not found");
    }

    @Override
    public ResponseEntity<String> pushBack(String id, String reason, Principal principal) {
        Optional<Vehicle> existing = repository.findById(id);
        if (existing.isPresent()) {
            Vehicle vehicle = existing.get();

            String originalStatus = vehicle.getStatus();
            vehicle.setStatus("PUSHED_BACK");
            vehicle.setReason(reason);
            vehicle.setUpdatedBy(principal.getName());
            vehicle.setUpdatedAt(LocalDate.now());

            repository.save(vehicle);

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("PUSHED_BACK")
                    .description("Vehicle pushed back. Status changed from '" + originalStatus +
                            "' to 'PUSHED_BACK'. Reason: '" + reason + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "vehicle_audit_trail");

            return ResponseEntity.ok("Vehicle pushed back successfully");
        }
        return ResponseEntity.status(404).body("Vehicle not found");
    }

    @Override
    public ResponseEntity<String> getAllPendingVehicles() {
        List<Vehicle> vehicles = repository.findByStatus("PENDING");
        return ResponseEntity.ok(vehicles.toString());
    }

    @Override
    public ResponseEntity<String> getAllApprovedVehicles() {
        List<Vehicle> vehicles = repository.findByStatus("APPROVED");
        return ResponseEntity.ok(vehicles.toString());
    }

    @Override
    public ResponseEntity<String> getAllRejectedVehicles() {
        List<Vehicle> vehicles = repository.findByStatus("REJECTED");
        return ResponseEntity.ok(vehicles.toString());
    }

    @Override
    public ResponseEntity<String> getAllPushedBackVehicles() {
        List<Vehicle> vehicles = repository.findByStatus("PUSHED_BACK");
        return ResponseEntity.ok(vehicles.toString());
    }

    @Override
    public long getApprovedVehicleCount() {
        return repository.countByStatusIgnoreCase("APPROVED");
    }
}
