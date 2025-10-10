package com.commstack.coapp.ServiceImplementation;

import com.commstack.coapp.Models.Departments;
import com.commstack.coapp.Models.UserAuditTrail;
import com.commstack.coapp.Repositories.DepartmentsRepository;
import com.commstack.coapp.Service.DepartmentsService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DepartmentsServiceImpl implements DepartmentsService {

    private final DepartmentsRepository departmentsRepository;
    private final MongoTemplate mongoTemplate;

    public DepartmentsServiceImpl(DepartmentsRepository departmentsRepository, MongoTemplate mongoTemplate) {
        this.departmentsRepository = departmentsRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Departments create(Departments departments, Principal principal) {
        String user = principal != null ? principal.getName() : "system";
        departments.setCreatedBy(user);
        departments.setCreatedAt(LocalDateTime.now());
        Departments saved = departmentsRepository.save(departments);

        try {
            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(saved.getId())
                    .action("CREATE_DEPARTMENT")
                    .description("Created department: " + saved.getName())
                    .doneBy(user)
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "departments_audit_trail");
        } catch (Exception e) {
            System.err.println("Failed to write department audit: " + e.getMessage());
        }

        return saved;
    }

    @Override
    public List<Departments> getAll() {
        return departmentsRepository.findAll();
    }

    @Override
    public Departments getById(String id) {
        Optional<Departments> opt = departmentsRepository.findById(id);
        return opt.orElse(null);
    }

    @Override
    public Departments update(String id, Departments departments, Principal principal) {
        Optional<Departments> opt = departmentsRepository.findById(id);
        if (!opt.isPresent())
            return null;
        Departments existing = opt.get();
        Departments original = Departments.builder()
                .id(existing.getId())
                .name(existing.getName())
                .code(existing.getCode())
                .description(existing.getDescription())
                .createdBy(existing.getCreatedBy())
                .createdAt(existing.getCreatedAt())
                .build();

        existing.setName(departments.getName());
        existing.setCode(departments.getCode());
        existing.setDescription(departments.getDescription());
        existing.setUpdatedBy(principal != null ? principal.getName() : "system");
        existing.setUpdatedAt(LocalDateTime.now());
        Departments saved = departmentsRepository.save(existing);

        try {
            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(saved.getId())
                    .action("UPDATE_DEPARTMENT")
                    .description("Updated department. before=" + original + ", after=" + saved)
                    .doneBy(saved.getUpdatedBy())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "departments_audit_trail");
        } catch (Exception e) {
            System.err.println("Failed to write department update audit: " + e.getMessage());
        }

        return saved;
    }

    @Override
    public void delete(String id) {
        Optional<Departments> opt = departmentsRepository.findById(id);
        if (opt.isPresent()) {
            Departments existing = opt.get();
            departmentsRepository.deleteById(id);
            try {
                UserAuditTrail audit = UserAuditTrail.builder()
                        .userId(existing.getId())
                        .action("DELETE_DEPARTMENT")
                        .description("Deleted department: " + existing.getName())
                        .doneBy(existing.getUpdatedBy() != null ? existing.getUpdatedBy() : existing.getCreatedBy())
                        .dateTime(LocalDateTime.now())
                        .build();
                mongoTemplate.save(audit, "departments_audit_trail");
            } catch (Exception e) {
                System.err.println("Failed to write department delete audit: " + e.getMessage());
            }
        }
    }

    @Override
    public Departments findByCode(String code) {
        Optional<Departments> opt = departmentsRepository.findByCode(code);
        return opt.orElse(null);
    }
}
