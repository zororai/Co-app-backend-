package com.commstack.coapp.Service;

import com.commstack.coapp.Models.SHEActivity;
import org.springframework.http.ResponseEntity;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

public interface SHEActivityService {
    // Basic CRUD operations
    ResponseEntity<String> create(SHEActivity activity, Principal principal);

    List<SHEActivity> getAll();

    SHEActivity getById(String id);

    ResponseEntity<String> update(String id, SHEActivity activity, Principal principal);

    ResponseEntity<String> delete(String id, Principal principal);

    // Status management
    ResponseEntity<String> approval(String id, Principal principal);

    ResponseEntity<String> reject(String id, String reason, Principal principal);

    ResponseEntity<String> pushBack(String id, String reason, Principal principal);

    ResponseEntity<String> complete(String id, Principal principal);

    // Status queries
    ResponseEntity<String> getAllPendingActivities();

    ResponseEntity<String> getAllApprovedActivities();

    ResponseEntity<String> getAllRejectedActivities();

    ResponseEntity<String> getAllPushedBackActivities();

    ResponseEntity<String> getAllCompletedActivities();

    // Specialized queries
    List<SHEActivity> findByActivityType(String activityType);

    List<SHEActivity> findByDate(LocalDate date);

    List<SHEActivity> findByDateRange(LocalDate startDate, LocalDate endDate);

    List<SHEActivity> findByLocation(String location);

    List<SHEActivity> findByConductedBy(String conductedBy);

    List<SHEActivity> findByDepartment(String department);

    List<SHEActivity> findBySupervisor(String supervisor);

    List<SHEActivity> findByRiskLevel(String riskLevel);

    // Follow-up management
    List<SHEActivity> findActivitiesRequiringFollowUp();

    List<SHEActivity> findOverdueFollowUps(LocalDate date);

    List<SHEActivity> findOverdueActions(LocalDate date);

    // Metrics
    ResponseEntity<String> getComplianceMetrics(LocalDate startDate, LocalDate endDate);

    ResponseEntity<String> getSafetyMetrics(LocalDate startDate, LocalDate endDate);

    ResponseEntity<String> getParticipationMetrics(LocalDate startDate, LocalDate endDate);
}
