package com.commstack.coapp.ServiceImplementation;

import com.commstack.coapp.Models.TransportCostonboarding;
import com.commstack.coapp.Repositories.TransportCostonboardingRepository;
import com.commstack.coapp.Service.TransportCostonboardingService;
import com.commstack.coapp.Models.UserAuditTrail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransportCostonboardingServiceImpl implements TransportCostonboardingService {
    @Autowired
    private TransportCostonboardingRepository repository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public TransportCostonboarding create(TransportCostonboarding transportCostonboarding, Principal principal) {
        transportCostonboarding.setStatus("PENDING");
        TransportCostonboarding saved = repository.save(transportCostonboarding);
        UserAuditTrail audit = UserAuditTrail.builder()
                .userId(saved.getId())
                .action("CREATED")
                .description("Created transport cost onboarding")
                .doneBy(principal.getName())
                .dateTime(LocalDateTime.now())
                .build();
        mongoTemplate.save(audit, "user_audit_trail");
        return saved;
    }

    @Override
    public TransportCostonboarding pushBack(String id, String reason, Principal principal) {
        Optional<TransportCostonboarding> result = repository.findById(id);
        if (result.isPresent()) {
            TransportCostonboarding entity = result.get();
            entity.setStatus("PUSHED_BACK");
            entity.setReason(reason);
            TransportCostonboarding updated = repository.save(entity);
            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("PUSHED_BACK")
                    .description("Pushed back transport cost onboarding, reason: " + reason)
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "user_audit_trail");
            return updated;
        }
        return null;
    }

    @Override
    public TransportCostonboarding approve(String id, Principal principal) {
        Optional<TransportCostonboarding> result = repository.findById(id);
        if (result.isPresent()) {
            TransportCostonboarding entity = result.get();
            entity.setStatus("APPROVED");
            entity.setReason(null);
            TransportCostonboarding updated = repository.save(entity);
            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("APPROVED")
                    .description("Approved transport cost onboarding")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "user_audit_trail");
            return updated;
        }
        return null;
    }

    @Override
    public TransportCostonboarding reject(String id, Principal principal) {
        Optional<TransportCostonboarding> result = repository.findById(id);
        if (result.isPresent()) {
            TransportCostonboarding entity = result.get();
            entity.setStatus("REJECTED");
            TransportCostonboarding updated = repository.save(entity);
            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("REJECTED")
                    .description("Rejected transport cost onboarding")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "user_audit_trail");
            return updated;
        }
        return null;
    }

    @Override
    public List<TransportCostonboarding> getAll() {
        return repository.findAll();
    }

    @Override
    public TransportCostonboarding getById(String id) {
        Optional<TransportCostonboarding> result = repository.findById(id);
        return result.orElse(null);
    }
}
