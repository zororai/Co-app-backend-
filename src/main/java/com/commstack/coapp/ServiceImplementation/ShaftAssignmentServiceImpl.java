
package com.commstack.coapp.ServiceImplementation;

import com.commstack.coapp.Repositories.SectionRepository;
import com.commstack.coapp.Models.Section;

import com.commstack.coapp.Repositories.CompanyRegistrationRepository;
import com.commstack.coapp.Repositories.RegMinerRepository;
import com.commstack.coapp.Models.Regminer;
import com.commstack.coapp.Models.UserAuditTrail;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.security.Principal;
import java.time.LocalDateTime;
import com.commstack.coapp.Models.ShaftAssignment;
import com.commstack.coapp.Repositories.ShaftAssignmentRepository;
import com.commstack.coapp.Service.ShaftAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShaftAssignmentServiceImpl implements ShaftAssignmentService {

    public List<ShaftAssignment> getByMinerId(String minerId) {
        return repository.findAll().stream()
                .filter(s -> minerId != null && minerId.equals(String.valueOf(s.getMinerId())))
                .toList();
    }

    @Autowired
    private SectionRepository sectionRepository;

    public ShaftAssignment pushBack(String id, String reason, Principal principal) {
        Optional<ShaftAssignment> result = repository.findById(id);
        if (result.isPresent()) {
            ShaftAssignment assignment = result.get();
            assignment.setStatus("PUSHED_BACK");
            assignment.setReason(reason);
            assignment.setUpdatedBy(principal.getName());
            assignment.setUpdatedAt(LocalDateTime.now().toLocalDate());
            ShaftAssignment updated = repository.save(assignment);
            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("PUSHED_BACK")
                    .description(
                            "Pushed back shaft assignment for minerId: " + updated.getMinerId() + ", reason: " + reason)
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "user_audit_trail");
            return updated;
        }
        return null;
    }

    public List<ShaftAssignment> getAllApproved() {
        return repository.findAll().stream()
                .filter(s -> "APPROVED".equalsIgnoreCase(s.getStatus()))
                .toList();
    }

    public ShaftAssignment approve(String id, Principal principal) {
        Optional<ShaftAssignment> result = repository.findById(id);
        if (result.isPresent()) {
            ShaftAssignment assignment = result.get();
            assignment.setStatus("APPROVED");
            assignment.setUpdatedBy(principal.getName());
            assignment.setUpdatedAt(LocalDateTime.now().toLocalDate());
            assignment.setReason(null);
            ShaftAssignment updated = repository.save(assignment);
            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("APPROVED")
                    .description("Approved shaft assignment for minerId: " + updated.getMinerId())
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "user_audit_trail");
            return updated;
        }
        return null;
    }

    @Autowired
    private RegMinerRepository regMinerRepository;
    @Autowired
    private CompanyRegistrationRepository companyRegistrationRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ShaftAssignmentRepository repository;

    @Override
    public ShaftAssignment create(ShaftAssignment shaftAssignment, Principal principal) {
        // Check if creating this shaft exceeds the allowed number in Section

        shaftAssignment.setCreatedBy(principal.getName());
        shaftAssignment.setCreatedAt(LocalDateTime.now().toLocalDate());
        shaftAssignment.setUpdatedBy(principal.getName());
        shaftAssignment.setUpdatedAt(LocalDateTime.now().toLocalDate());
        shaftAssignment.setStatus("PENDING"); // Assuming reason is not set during creation
        ShaftAssignment saved = repository.save(shaftAssignment);
        // Update shaftnumber in Regminer or CompanyRegistration if minerId matches
        boolean updated = false;
        if (saved.getMinerId() != null && !saved.getMinerId().isEmpty()) {
            // Check if the minerId exists in Regminer
            // Assuming minerId is a String, adjust as necessary
            String minerIdStr = String.valueOf(saved.getMinerId());
            Optional<Regminer> regminerOpt = regMinerRepository.findById(minerIdStr);

            if (regminerOpt.isPresent()) {
                Regminer regminer = regminerOpt.get();

                // Get the current shaft number
                Integer shaftNumberObj = regminer.getShaftnumber();
                int currentShaftNumber = shaftNumberObj != null ? shaftNumberObj : 0;

                // Increment by 1
                regminer.setShaftnumber(currentShaftNumber + 1);

                regMinerRepository.save(regminer);
                updated = true;
            }

            // If not found in Regminer, try CompanyRegistration
            if (!updated) {
                companyRegistrationRepository.findById(minerIdStr).ifPresent(company -> {
                    Integer shaftNumberObj = company.getShaftnumber();
                    int currentShaftNumber = shaftNumberObj != null ? shaftNumberObj : 0;
                    company.setShaftnumber(currentShaftNumber + 1);
                    companyRegistrationRepository.save(company);
                });
            }
        }

        UserAuditTrail audit = UserAuditTrail.builder()
                .userId(saved.getId())
                .action("CREATED")
                .description("Created shaft assignment for minerId: " + saved.getMinerId())
                .doneBy(principal.getName())
                .dateTime(LocalDateTime.now())
                .build();
        mongoTemplate.save(audit, "user_audit_trail");
        return saved;
    }

    @Override
    public ShaftAssignment getById(String id) {
        Optional<ShaftAssignment> result = repository.findById(id);
        return result.orElse(null);
    }

    @Override
    public List<ShaftAssignment> getAll() {
        return repository.findAll();
    }

    @Override
    public ShaftAssignment update(String id, ShaftAssignment shaftAssignment, Principal principal) {
        shaftAssignment.setUpdatedBy(principal.getName());
        shaftAssignment.setUpdatedAt(LocalDateTime.now().toLocalDate());
        shaftAssignment.setStatus("PENDING");

        if (repository.existsById(id)) {
            shaftAssignment.setId(id);
            ShaftAssignment updated = repository.save(shaftAssignment);
            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("UPDATED")
                    .description("Updated shaft assignment for minerId: " + updated.getMinerId())
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "user_audit_trail");
            return updated;
        }
        return null;
    }

    @Override
    public void delete(String id, Principal principal) {
        if (!repository.existsById(id)) {
            return; // Handle case where the ID does not exist
        }
        repository.deleteById(id);
        UserAuditTrail audit = UserAuditTrail.builder()
                .userId(id)
                .action("DELETED")
                .description("Deleted shaft assignment with id: " + id)
                .doneBy(principal.getName())
                .dateTime(LocalDateTime.now())
                .build();
        mongoTemplate.save(audit, "user_audit_trail");
    }
}
