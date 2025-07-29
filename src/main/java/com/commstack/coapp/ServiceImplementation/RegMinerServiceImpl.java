package com.commstack.coapp.ServiceImplementation;

import com.commstack.coapp.Models.Regminer;
import com.commstack.coapp.Models.MinerAuditTrail;
import com.commstack.coapp.Repositories.RegMinerRepository;
import com.commstack.coapp.Service.RegMinerService;
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
public class RegMinerServiceImpl implements RegMinerService {

    @Autowired
    private RegMinerRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public ResponseEntity<String> create(Regminer miner, Principal principal) {
        // Set creation metadata
        miner.setCreatedby(principal.getName());
        miner.setCreatedAt(LocalDate.now());
        miner.setUpdatedby(principal.getName());
        miner.setUpdatedAt(LocalDate.now());
        miner.setStatus("PENDING");

        // Save the miner
        Regminer savedMiner = repository.save(miner);

        // Create audit trail
        MinerAuditTrail audit = MinerAuditTrail.builder()
                .minerId(savedMiner.getId())
                .action("CREATED")
                .description("New miner created with name: '" + miner.getName() + " " + miner.getSurname() +
                        "', registration number: '" + miner.getRegistrationNumber() + "'")
                .doneBy(principal.getName())
                .dateTime(LocalDateTime.now())
                .build();
        mongoTemplate.save(audit, "miner_audit_trail");

        return ResponseEntity.ok("Miner created successfully");
    }

    public ResponseEntity<String> approval(String id, Principal principal) {
        Optional<Regminer> existing = repository.findById(id);
        if (existing.isPresent()) {
            Regminer miner = existing.get();

            // Check if miner is in PENDING status
            if (!"PENDING".equals(miner.getStatus())) {
                return ResponseEntity.status(400).body("Miner is not in PENDING status");
            }

            // Update status
            String originalStatus = miner.getStatus();
            miner.setStatus("APPROVED");
            miner.setUpdatedby(principal.getName());
            miner.setUpdatedAt(LocalDate.now());

            // Save the miner
            repository.save(miner);

            // Create audit trail
            MinerAuditTrail audit = MinerAuditTrail.builder()
                    .minerId(id)
                    .action("APPROVED")
                    .description("Miner status changed from '" + originalStatus + "' to 'APPROVED' for miner: '" +
                            miner.getName() + " " + miner.getSurname() +
                            "', registration number: '" + miner.getRegistrationNumber() + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "miner_audit_trail");

            return ResponseEntity.ok("Miner approved successfully");
        }
        return ResponseEntity.status(404).body("Miner not found");
    }

    public ResponseEntity<String> reject(String id, String reason, Principal principal) {
        Optional<Regminer> existing = repository.findById(id);
        if (existing.isPresent()) {
            Regminer miner = existing.get();

            // Check if miner is in PENDING status
            if (!"PENDING".equals(miner.getStatus())) {
                return ResponseEntity.status(400).body("Miner is not in PENDING status");
            }

            // Update status and reason
            miner.setStatus("REJECTED");
            miner.setReason(reason);
            miner.setUpdatedby(principal.getName());
            miner.setUpdatedAt(LocalDate.now());

            // Save the miner
            repository.save(miner);

            // Create audit trail
            MinerAuditTrail audit = MinerAuditTrail.builder()
                    .minerId(id)
                    .action("REJECTED")
                    .description("Miner rejected with reason: '" + reason + "' for miner: '" +
                            miner.getName() + " " + miner.getSurname() +
                            "', registration number: '" + miner.getRegistrationNumber() + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "miner_audit_trail");

            return ResponseEntity.ok("Miner rejected successfully");
        }
        return ResponseEntity.status(404).body("Miner not found");
    }

    public ResponseEntity<String> pushBack(String id, String reason, Principal principal) {
        Optional<Regminer> existing = repository.findById(id);
        if (existing.isPresent()) {
            Regminer miner = existing.get();

            // Check if miner is in PENDING status
            if (!"PENDING".equals(miner.getStatus())) {
                return ResponseEntity.status(400).body("Miner is not in PENDING status");
            }

            // Update status and reason
            miner.setStatus("PUSHED_BACK");
            miner.setReason(reason);
            miner.setUpdatedby(principal.getName());
            miner.setUpdatedAt(LocalDate.now());

            // Save the miner
            repository.save(miner);

            // Create audit trail
            MinerAuditTrail audit = MinerAuditTrail.builder()
                    .minerId(id)
                    .action("PUSHED_BACK")
                    .description("Miner pushed back with reason: '" + reason + "' for miner: '" +
                            miner.getName() + " " + miner.getSurname() +
                            "', registration number: '" + miner.getRegistrationNumber() + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "miner_audit_trail");

            return ResponseEntity.ok("Miner pushed back successfully");
        }
        return ResponseEntity.status(404).body("Miner not found");
    }

    public ResponseEntity<String> getallApprovedMiners() {
        List<Regminer> approvedMiners = repository.findAll().stream()
                .filter(miner -> "APPROVED".equals(miner.getStatus()))
                .toList();
        return ResponseEntity.ok(approvedMiners.toString());
    }

    public ResponseEntity<String> getallRejectedMiners() {
        List<Regminer> rejectedMiners = repository.findAll().stream()
                .filter(miner -> "REJECTED".equals(miner.getStatus()))
                .toList();
        return ResponseEntity.ok(rejectedMiners.toString());
    }

    public ResponseEntity<String> getallPushedBackMiners() {
        List<Regminer> pushedBackMiners = repository.findAll().stream()
                .filter(miner -> "PUSHED_BACK".equals(miner.getStatus()))
                .toList();
        return ResponseEntity.ok(pushedBackMiners.toString());
    }

    public ResponseEntity<String> getallPendingMiners() {
        List<Regminer> pendingMiners = repository.findAll().stream()
                .filter(miner -> "PENDING".equals(miner.getStatus()))
                .toList();
        return ResponseEntity.ok(pendingMiners.toString());
    }

    @Override
    public List<Regminer> getAll() {
        return repository.findAll();
    }

    @Override
    public Regminer getById(String id) {
        return repository.findById(id).orElse(null);
    }

    public ResponseEntity<String> update(String id, Regminer miner, Principal principal) {
        Optional<Regminer> existing = repository.findById(id);
        if (existing.isPresent()) {
            Regminer existingMiner = existing.get();

            // Save original values for audit trail
            String originalName = existingMiner.getName();
            String originalSurname = existingMiner.getSurname();
            String originalStatus = existingMiner.getStatus();

            // Update fields
            existingMiner.setName(miner.getName());
            existingMiner.setSurname(miner.getSurname());
            existingMiner.setNationIdNumber(miner.getNationIdNumber());
            existingMiner.setAddress(miner.getAddress());
            existingMiner.setCellNumber(miner.getCellNumber());
            existingMiner.setEmail(miner.getEmail());
            existingMiner.setStatus(miner.getStatus());
            existingMiner.setReason(miner.getReason());
            existingMiner.setRegistrationNumber(miner.getRegistrationNumber());
            existingMiner.setCooperativeDetails(miner.getCooperativeDetails());
            existingMiner.setPosition(miner.getPosition());
            existingMiner.setIdPicture(miner.getIdPicture());
            existingMiner.setTeamMembers(miner.getTeamMembers());
            existingMiner.setUpdatedby(principal.getName());
            existingMiner.setUpdatedAt(LocalDate.now());

            // Save updated miner
            repository.save(existingMiner);

            // Create audit trail
            StringBuilder description = new StringBuilder("Miner updated. Changes: ");
            if (!originalName.equals(miner.getName())) {
                description.append("Name changed from '").append(originalName).append("' to '").append(miner.getName())
                        .append("'. ");
            }
            if (!originalSurname.equals(miner.getSurname())) {
                description.append("Surname changed from '").append(originalSurname).append("' to '")
                        .append(miner.getSurname()).append("'. ");
            }
            if (originalStatus != null && !originalStatus.equals(miner.getStatus())) {
                description.append("Status changed from '").append(originalStatus).append("' to '")
                        .append(miner.getStatus()).append("'.");
            }

            MinerAuditTrail audit = MinerAuditTrail.builder()
                    .minerId(id)
                    .action("UPDATED")
                    .description(description.toString())
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "miner_audit_trail");

            return ResponseEntity.ok("Miner updated successfully");
        }
        return ResponseEntity.status(404).body("Miner not found");
    }

    @Override
    public ResponseEntity<String> delete(String id, Principal principal) {
        Optional<Regminer> minerToDelete = repository.findById(id);
        if (minerToDelete.isPresent()) {
            Regminer miner = minerToDelete.get();

            // Create audit trail before deleting
            MinerAuditTrail audit = MinerAuditTrail.builder()
                    .minerId(id)
                    .action("DELETED")
                    .description("Miner deleted: '" + miner.getName() + " " + miner.getSurname() +
                            "', registration number: '" + miner.getRegistrationNumber() + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "miner_audit_trail");

            // Delete the miner
            repository.deleteById(id);
            return ResponseEntity.ok("Miner deleted successfully");
        }
        return ResponseEntity.status(404).body("Miner not found");
    }

}
