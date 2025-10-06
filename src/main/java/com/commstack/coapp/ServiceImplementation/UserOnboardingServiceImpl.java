package com.commstack.coapp.ServiceImplementation;

import com.commstack.coapp.Models.UserOnboarding;
import com.commstack.coapp.Models.permissions;
import com.commstack.coapp.Models.Mill;
import com.commstack.coapp.Models.Signup;
import com.commstack.coapp.Models.UserAuditTrail;
import com.commstack.coapp.Repositories.UserOnboardingRepository;
import com.commstack.coapp.Service.AuthenticationService;
import com.commstack.coapp.Service.UserOnboardingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserOnboardingServiceImpl implements UserOnboardingService {

    private static final Logger logger = LoggerFactory.getLogger(UserOnboardingServiceImpl.class);

    @Autowired
    private UserOnboardingRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    public ResponseEntity<String> create(UserOnboarding user, Principal principal) {
        try {
            logger.info("Creating new user onboarding for email: {}", user.getEmail());

            // Validate required fields
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Email is required");
            }
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Password is required");
            }
            if (user.getName() == null || user.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Name is required");
            }

            // First create authentication user
            Signup signupRequest = Signup.builder()
                    .fullName(user.getName() + " " + user.getSurname())
                    .email(user.getEmail())
                    .password(user.getPassword())
                    .build();

            logger.info("Attempting to create authentication user for: {}", user.getEmail());
            ResponseEntity<Object> signupResponse = authenticationService.signup(signupRequest, principal);

            // Check if signup was successful
            if (signupResponse.getStatusCodeValue() != 200) {
                logger.error("Authentication user creation failed for {}: {}", user.getEmail(),
                        signupResponse.getBody());
                return ResponseEntity.status(signupResponse.getStatusCodeValue())
                        .body("Failed to create authentication user: " + signupResponse.getBody());
            }

            logger.info("Authentication user created successfully for: {}", user.getEmail());

            // Set creation metadata
            user.setCreatedBy(principal.getName());
            user.setCreatedAt(LocalDate.now());
            user.setUpdatedBy(principal.getName());
            user.setUpdatedAt(LocalDate.now());
            user.setStatus("PENDING");

            // Save the user
            UserOnboarding savedUser = repository.save(user);
            logger.info("UserOnboarding saved with ID: {}", savedUser.getId());

            // Create audit trail
            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(savedUser.getId())
                    .action("CREATED")
                    .description("New user created with name: '" + user.getName() + " " + user.getSurname() +
                            "', position: '" + user.getPosition() + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "user_audit_trail");

            logger.info("User onboarding process completed successfully for: {}", user.getEmail());
            return ResponseEntity.ok("User created successfully");

        } catch (Exception e) {
            logger.error("Error creating user onboarding for {}: {}", user.getEmail(), e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body("Error creating user: " + e.getMessage());
        }
    }

    @Override
    public List<UserOnboarding> getAll() {
        return repository.findAll();
    }

    @Override
    public UserOnboarding getById(String id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public ResponseEntity<String> update(String id, UserOnboarding user, Principal principal) {
        Optional<UserOnboarding> existing = repository.findById(id);
        if (existing.isPresent()) {
            UserOnboarding existingUser = existing.get();

            // Save original values for audit trail
            String originalStatus = existingUser.getStatus();
            String originalPosition = existingUser.getPosition();
            String originalRole = existingUser.getRole();

            // Update fields
            existingUser.setName(user.getName());
            existingUser.setSurname(user.getSurname());
            existingUser.setIdNumber(user.getIdNumber());
            existingUser.setAddress(user.getAddress());
            existingUser.setCellNumber(user.getCellNumber());
            existingUser.setEmail(user.getEmail());
            existingUser.setPosition(user.getPosition());
            existingUser.setRole(user.getRole());
            existingUser.setStatus(user.getStatus());
            existingUser.setUpdatedBy(principal.getName());
            existingUser.setUpdatedAt(LocalDate.now());

            // Save updated user
            repository.save(existingUser);

            // Create audit trail
            StringBuilder description = new StringBuilder("User updated. Changes: ");
            if (!originalStatus.equals(user.getStatus())) {
                description.append("Status changed from '").append(originalStatus).append("' to '")
                        .append(user.getStatus()).append("'. ");
            }
            if (!originalPosition.equals(user.getPosition())) {
                description.append("Position changed from '").append(originalPosition).append("' to '")
                        .append(user.getPosition()).append("'. ");
            }
            if (!originalRole.equals(user.getRole())) {
                description.append("Role changed from '").append(originalRole).append("' to '").append(user.getRole())
                        .append("'.");
            }

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("UPDATED")
                    .description(description.toString())
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "user_audit_trail");

            return ResponseEntity.ok("User updated successfully");
        }
        return ResponseEntity.status(404).body("User not found");
    }

    @Override
    public ResponseEntity<String> delete(String id, Principal principal) {
        Optional<UserOnboarding> userToDelete = repository.findById(id);
        if (userToDelete.isPresent()) {
            UserOnboarding user = userToDelete.get();

            // Create audit trail before deleting
            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("DELETED")
                    .description("User deleted: '" + user.getName() + " " + user.getSurname() +
                            "', position: '" + user.getPosition() + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "user_audit_trail");

            // Delete the user
            repository.deleteById(id);
            return ResponseEntity.ok("User deleted successfully");
        }
        return ResponseEntity.status(404).body("User not found");
    }

    @Override
    public ResponseEntity<String> approval(String id, Principal principal) {
        Optional<UserOnboarding> existing = repository.findById(id);
        if (existing.isPresent()) {
            UserOnboarding user = existing.get();

            // Check if user is in PENDING status
            if (!"PENDING".equals(user.getStatus())) {
                return ResponseEntity.status(400).body("User is not in PENDING status");
            }

            // Update status
            String originalStatus = user.getStatus();
            user.setStatus("APPROVED");
            user.setUpdatedBy(principal.getName());
            user.setUpdatedAt(LocalDate.now());

            // Save the user
            repository.save(user);

            // Create audit trail
            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("APPROVED")
                    .description("User status changed from '" + originalStatus + "' to 'APPROVED' for user: '" +
                            user.getName() + " " + user.getSurname() + "', position: '" + user.getPosition() + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "user_audit_trail");

            return ResponseEntity.ok("User approved successfully");
        }
        return ResponseEntity.status(404).body("User not found");
    }

    @Override
    public ResponseEntity<String> reject(String id, String reason, Principal principal) {
        Optional<UserOnboarding> existing = repository.findById(id);
        if (existing.isPresent()) {
            UserOnboarding user = existing.get();

            String originalStatus = user.getStatus();
            user.setStatus("REJECTED");
            user.setReason(reason);
            user.setUpdatedBy(principal.getName());
            user.setUpdatedAt(LocalDate.now());

            repository.save(user);

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("REJECTED")
                    .description("User rejected. Status changed from '" + originalStatus + "' to 'REJECTED'. Reason: '"
                            + reason + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "user_audit_trail");

            return ResponseEntity.ok("User rejected successfully");
        }
        return ResponseEntity.status(404).body("User not found");
    }

    @Override
    public ResponseEntity<String> pushBack(String id, String reason, Principal principal) {
        Optional<UserOnboarding> existing = repository.findById(id);
        if (existing.isPresent()) {
            UserOnboarding user = existing.get();

            String originalStatus = user.getStatus();
            user.setStatus("PUSHED_BACK");
            user.setReason(reason);
            user.setUpdatedBy(principal.getName());
            user.setUpdatedAt(LocalDate.now());

            repository.save(user);

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("PUSHED_BACK")
                    .description("User pushed back. Status changed from '" + originalStatus
                            + "' to 'PUSHED_BACK'. Reason: '" + reason + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "user_audit_trail");

            return ResponseEntity.ok("User pushed back successfully");
        }
        return ResponseEntity.status(404).body("User not found");
    }

    @Override
    public ResponseEntity<String> getAllPendingUsers() {
        List<UserOnboarding> users = repository.findByStatus("PENDING");
        return ResponseEntity.ok(users.toString());
    }

    @Override
    public ResponseEntity<String> getAllApprovedUsers() {
        List<UserOnboarding> users = repository.findByStatus("APPROVED");
        return ResponseEntity.ok(users.toString());
    }

    @Override
    public ResponseEntity<String> getAllRejectedUsers() {
        List<UserOnboarding> users = repository.findByStatus("REJECTED");
        return ResponseEntity.ok(users.toString());
    }

    @Override
    public ResponseEntity<String> getAllPushedBackUsers() {
        List<UserOnboarding> users = repository.findByStatus("PUSHED_BACK");
        return ResponseEntity.ok(users.toString());
    }

    @Override
    public ResponseEntity<String> getEmailResponse(String email) {
        try {
            logger.info("Received email request for: {}", email);

            // Validate email input
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Email parameter is required");
            }

            // Return the email
            return ResponseEntity.ok(email);

        } catch (Exception e) {
            logger.error("Error processing email request: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error processing email request: " + e.getMessage());
        }
    }

    public ResponseEntity<UserOnboarding> getByEmail(String email) {
        try {
            logger.info("Fetching UserOnboarding by email: {}", email);
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Optional<UserOnboarding> opt = repository.findById(email);
            return opt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error fetching user by email {}: {}", email, e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }
}
