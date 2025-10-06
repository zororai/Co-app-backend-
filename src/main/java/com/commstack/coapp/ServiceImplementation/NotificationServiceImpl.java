package com.commstack.coapp.ServiceImplementation;

import com.commstack.coapp.Models.Notification;
import com.commstack.coapp.Models.UserAuditTrail;
import com.commstack.coapp.Repositories.NotificationRepository;
import com.commstack.coapp.Service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public ResponseEntity<Notification> create(Notification notification, Principal principal) {
        notification.setCreatedBy(principal != null ? principal.getName() : "SYSTEM");
        notification.setCreatedAt(LocalDateTime.now());

        Notification saved = repository.save(notification);

        UserAuditTrail audit = UserAuditTrail.builder()
                .userId(saved.getId())
                .action("NOTIFICATION_CREATED")
                .description("Notification created: '" + saved.getTitle() + "'")
                .doneBy(principal != null ? principal.getName() : "SYSTEM")
                .dateTime(LocalDateTime.now())
                .build();
        mongoTemplate.save(audit, "notification_audit_trail");

        return ResponseEntity.ok(saved);
    }

    @Override
    public List<Notification> getAll() {
        return repository.findAll();
    }

    @Override
    public Notification getById(String id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public ResponseEntity<String> delete(String id, Principal principal) {
        Optional<Notification> existing = repository.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.status(404).body("Notification not found");
        }

        Notification n = existing.get();

        UserAuditTrail audit = UserAuditTrail.builder()
                .userId(id)
                .action("NOTIFICATION_DELETED")
                .description("Notification deleted: '" + n.getTitle() + "'")
                .doneBy(principal != null ? principal.getName() : "SYSTEM")
                .dateTime(LocalDateTime.now())
                .build();
        mongoTemplate.save(audit, "notification_audit_trail");

        repository.deleteById(id);
        return ResponseEntity.ok("Notification deleted");
    }
}
