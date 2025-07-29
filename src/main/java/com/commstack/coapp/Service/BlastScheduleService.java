package com.commstack.coapp.Service;

import com.commstack.coapp.Models.BlastSchedule;
import org.springframework.http.ResponseEntity;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

public interface BlastScheduleService {
    ResponseEntity<String> create(BlastSchedule schedule, Principal principal);

    List<BlastSchedule> getAll();

    BlastSchedule getById(String id);

    ResponseEntity<String> update(String id, BlastSchedule schedule, Principal principal);

    ResponseEntity<String> delete(String id, Principal principal);

    ResponseEntity<String> approval(String id, Principal principal);

    ResponseEntity<String> reject(String id, String reason, Principal principal);

    ResponseEntity<String> pushBack(String id, String reason, Principal principal);

    List<BlastSchedule> findByBlastZone(String blastZone);

    List<BlastSchedule> findByDate(LocalDate date);

    List<BlastSchedule> findByAssignedOperator(String assignedOperator);

    List<BlastSchedule> findBySafetyOfficer(String safetyOfficer);

    List<BlastSchedule> findByDateRange(LocalDate startDate, LocalDate endDate);

    List<BlastSchedule> findByBlastType(String blastType);

    ResponseEntity<String> getAllPendingSchedules();

    ResponseEntity<String> getAllApprovedSchedules();

    ResponseEntity<String> getAllRejectedSchedules();

    ResponseEntity<String> getAllPushedBackSchedules();
}
